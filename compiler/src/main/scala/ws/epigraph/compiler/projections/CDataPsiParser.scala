/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.compiler.projections

import ws.epigraph.compiler._
import ws.epigraph.psi.EpigraphPsiUtil
import ws.epigraph.schema.parser.psi._

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
object CDataPsiParser {

  def parseValue(expectedType: Option[CDataType], psi: SchemaDataValue, csf: CSchemaFile)
    (implicit ctx: CContext): CDataValue = {
    if (psi.getData != null)
      parseData(expectedType, psi.getData, csf)
    else if (psi.getDatum != null)
      parseDatum(expectedType.map(_.typeRef.resolved.asInstanceOf[CDatumType]), psi.getDatum, csf)
    else {
      ErrorReporter.reporter(csf).error("Neither data nor datum is set", psi)
      throw new CompilerException
    }
  }

  def parseData(expectedType: Option[CDataType], psi: SchemaData, csf: CSchemaFile)
    (implicit ctx: CContext): CData = {

    val typeOpt: Option[CDataType] = effectiveDataType(expectedType, psi.getTypeRef, csf)

    val tags = new mutable.LinkedHashMap[String, CDatum]

    for (entry: SchemaDataEntry <- psi.getDataEntryList) {
      val tagName = entry.getQid.getCanonicalName

      try {
        val expectedType: Option[CDatumType] = typeOpt.map{ t =>
          t.typeRef.resolved match {
            case vt: CEntityTypeDef =>
              val tag: CTag = vt.effectiveTags.find(_.name == tagName).getOrElse{
                ErrorReporter.reporter(csf).error(s"Tags '$tagName' doesn't exist in type '${vt.name.name}'",
                  entry.getQid)
                throw new CompilerException
              }
              tag.typeRef.resolved.asInstanceOf[CDatumType]
            case dt: CDatumType =>
              ErrorReporter.reporter(csf).error("Tags should not be specified for datum types", entry.getQid)
              throw new CompilerException
            case _ =>
              ErrorReporter.reporter(csf).error(s"Unknown type '${t.getClass.getName}'", psi)
              throw new CompilerException
          }
        }

        val valuePsi = entry.getDatum
        if (valuePsi == null)
          ErrorReporter.reporter(csf).error(s"No value for tag '$tagName' specified", psi)
        else
          tags.put(tagName, parseDatum(expectedType, valuePsi, csf))

      } catch {case _: CompilerException =>}
    }

    new CData(typeOpt, tags, EpigraphPsiUtil.getLocation(psi))
  }

  def parseDatum(expectedType: Option[CDatumType], psi: SchemaDatum, csf: CSchemaFile)
    (implicit ctx: CContext): CDatum = psi match {
    case srd: SchemaRecordDatum =>
      parseRecord(expectedType.map(_.asInstanceOf[CRecordTypeDef]), srd, csf)
    case smd: SchemaMapDatum =>
      parseMap(expectedType.map(_.asInstanceOf[CMapType]), smd, csf)
    case sld: SchemaListDatum =>
      parseList(expectedType.map(_.asInstanceOf[CListType]), sld, csf)
    case spd: SchemaPrimitiveDatum =>
      parsePrimitive(expectedType.map(_.asInstanceOf[CPrimitiveTypeDef]), spd, csf)
    case snd: SchemaNullDatum =>
      parseNull(expectedType, snd, csf)
    case _ =>
      ErrorReporter.reporter(csf).error(s"Unknown value type '${psi.getClass.getName}'", psi)
      throw new CompilerException
  }

  def parseRecord(expectedType: Option[CRecordTypeDef], psi: SchemaRecordDatum, csf: CSchemaFile)
    (implicit ctx: CContext): CRecordDatum = {

    val typeOpt = effectiveType(expectedType, psi.getTypeRef, csf)
    val fields = new mutable.LinkedHashMap[String, CDataValue]

    for (entry <- psi.getRecordDatumEntryList)
      try {
        val fieldName = entry.getQid.getCanonicalName

        val valuePsi = entry.getDataValue
        if (valuePsi == null) {
          ErrorReporter.reporter(csf).error(s"No value specified for field '$fieldName'", psi)
          throw new CompilerException
        }

        val fieldType: Option[CDataType] = typeOpt map { t =>
          val field = t.effectiveFields.find(_.name == fieldName).getOrElse{
            ErrorReporter.reporter(csf).error(s"Field '$fieldName' doesn't exist in type '${t.name.name}'", psi)
            throw new CompilerException
          }
          field.valueDataType
        }

        val value = parseValue(fieldType, valuePsi, csf)

        fields.put(fieldName, value)
      } catch {case _: CompilerException =>}

    new CRecordDatum(typeOpt, fields, EpigraphPsiUtil.getLocation(psi))
  }

  def parseMap(expectedType: Option[CMapType], psi: SchemaMapDatum, csf: CSchemaFile)
    (implicit ctx: CContext): CMapDatum = {

    val typeOpt = effectiveType(expectedType, psi.getTypeRef, csf)
    val keyTypeOpt: Option[CDatumType] = typeOpt.map(_.keyTypeRef.resolved.asInstanceOf[CDatumType])
    val valueTypeOpt: Option[CDataType] = typeOpt.map(_.valueDataType)

    val map = new mutable.LinkedHashMap[CDatum, CDataValue]

    for (entry <- psi.getMapDatumEntryList)
      try {
        val valuePsi = entry.getDataValue
        if (valuePsi == null) {
          ErrorReporter.reporter(csf).error(s"No value specified for key '${entry.getDatum.getText}'", psi)
          throw new CompilerException
        }

        val key = parseDatum(keyTypeOpt, entry.getDatum, csf)
        val value = parseValue(valueTypeOpt, valuePsi, csf)

        map.put(key, value)
      } catch {case _: CompilerException =>}

    new CMapDatum(typeOpt, map, EpigraphPsiUtil.getLocation(psi))
  }

  def parseList(expectedType: Option[CListType], psi: SchemaListDatum, csf: CSchemaFile)
    (implicit ctx: CContext): CListDatum = {

    val typeOpt = effectiveType(expectedType, psi.getTypeRef, csf)
    val elementTypeOpt = typeOpt.map(_.elementDataType)

    var items = List[CDataValue]()

    for (value <- psi.getDataValueList)
      try {
        val item = parseValue(elementTypeOpt, value, csf)
        items ::= item
      } catch {case _: CompilerException =>}

    new CListDatum(typeOpt, items.reverse, EpigraphPsiUtil.getLocation(psi))
  }

  def parsePrimitive(expectedType: Option[CPrimitiveTypeDef], psi: SchemaPrimitiveDatum, csf: CSchemaFile)
    (implicit ctx: CContext): CPrimitiveDatum = {

    val typeOpt: Option[CPrimitiveTypeDef] = effectiveType(expectedType, psi.getTypeRef, csf)

    val value: Any = typeOpt match {
      case Some(t) =>

        t.kind match {
          case CTypeKind.STRING =>
            val p = psi.getString
            if (p == null) {
              ErrorReporter.reporter(csf).error("String value expected", psi)
              throw new CompilerException
            }
            else {
              val text = p.getText
              text.substring(1, text.length - 1)
            }

          case CTypeKind.INTEGER =>
            val p = psi.getNumber
            if (p == null) {
              ErrorReporter.reporter(csf).error("Integer value expected", psi)
              0
            } else {
              try {
                p.getText.toInt
              } catch {
                case nfe: NumberFormatException =>
                  ErrorReporter.reporter(csf).error(s"Malformed integer value: ${nfe.getMessage}", psi)
                  throw new CompilerException
              }
            }

          case CTypeKind.LONG =>
            val p = psi.getNumber
            if (p == null) {
              ErrorReporter.reporter(csf).error("Long value expected", psi)
              0
            } else {
              try {
                p.getText.toLong
              } catch {
                case nfe: NumberFormatException =>
                  ErrorReporter.reporter(csf).error(s"Malformed long value: ${nfe.getMessage}", psi)
                  throw new CompilerException
              }
            }

          case CTypeKind.DOUBLE =>
            val p = psi.getNumber
            if (p == null) {
              ErrorReporter.reporter(csf).error("Double value expected", psi)
              0
            } else {
              try {
                p.getText.toDouble
              } catch {
                case nfe: NumberFormatException =>
                  ErrorReporter.reporter(csf).error(s"Malformed double value: ${nfe.getMessage}", psi)
                  throw new CompilerException
              }
            }

          case CTypeKind.BOOLEAN =>
            val p = psi.getBoolean
            if (p == null) {
              ErrorReporter.reporter(csf).error("Boolean value expected", psi)
              0
            } else {
              try {
                p.getText.toBoolean
              } catch {
                case iae: IllegalArgumentException =>
                  ErrorReporter.reporter(csf).error(s"Malformed boolean value: ${iae.getMessage}", psi)
                  throw new CompilerException
              }
            }

          case _ =>
            ErrorReporter.reporter(csf).error(s"Unsupported type kind: ${t.kind}", psi)
            throw new CompilerException

        }

      case None => // have to guess..
        if (psi.getString != null) {
          val text = psi.getString.getText
          text.substring(1, text.length - 1)
        } else if (psi.getBoolean != null) {
          psi.getBoolean.getText.toBoolean
        } else if (psi.getNumber != null) {
          val text = psi.getNumber.getText
          if (text.contains("."))
            text.toDouble
          else
            text.toLong
        } else {
          ErrorReporter.reporter(csf).error(s"Don't know how to handle primitive '${psi.getText}'", psi)
          throw new CompilerException
        }
    }

    new CPrimitiveDatum(
      typeOpt,
      value,
      EpigraphPsiUtil.getLocation(psi)
    )
  }

  def parseNull(expectedType: Option[CDatumType], psi: SchemaNullDatum, csf: CSchemaFile)
    (implicit ctx: CContext): CNullDatum = {
    val typeOpt = effectiveType(expectedType, psi.getTypeRef, csf)
    new CNullDatum(typeOpt, EpigraphPsiUtil.getLocation(psi))
  }

  private def effectiveType[T <: CType](expectedType: Option[T], typeRefPsi: SchemaTypeRef, csf: CSchemaFile)
    (implicit ctx: CContext): Option[T] = {
    val typeOpt = Option(typeRefPsi).map(CProjectionParsingUtil.parseType(_, csf))
    for (t <- typeOpt; et <- expectedType; if !et.isAssignableFrom(t))
      ErrorReporter.reporter(csf).error(s"Type '${t.name.name}' is not a sub-type of expected type '${et.name.name}'",
        typeRefPsi)

    if (typeOpt.isDefined) typeOpt.map(_.asInstanceOf[T])
    else if (expectedType.isDefined) expectedType
    else None
  }

  private def effectiveDataType(expectedType: Option[CDataType], typeRefPsi: SchemaTypeRef, csf: CSchemaFile)
    (implicit ctx: CContext): Option[CDataType] = {
    val typeOpt: Option[CType] = Option(typeRefPsi).map(CProjectionParsingUtil.parseType(_, csf))

    for (t <- typeOpt; et <- expectedType; if !et.typeRef.resolved.isAssignableFrom(t))
      ErrorReporter.reporter(csf).error(s"Type '${t.name.name}' is not a sub-type of expected type '${et.name}'",
        typeRefPsi)

    if (typeOpt.isDefined) typeOpt.map(t => new CDataType(csf, t.selfRef, None))
    else if (expectedType.isDefined) expectedType
    else None
  }

}
