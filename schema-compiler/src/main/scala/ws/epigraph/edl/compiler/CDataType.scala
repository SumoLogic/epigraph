/*
 * Copyright 2016 Sumo Logic
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

/* Created by yegor on 8/23/16. */

package ws.epigraph.edl.compiler

import ws.epigraph.edl.parser.psi.{SchemaDefaultOverride, SchemaValueTypeRef}
import org.jetbrains.annotations.Nullable

/**
 * Container (field value, list element, or map value) data type.
 */
final class CDataType( // TODO split into CVarDataType and CDatumDataType?
    val csf: CSchemaFile,
    val typeRef: CTypeRef,
    private val defaultTagNameDecl: Option[String]
)(implicit val ctx: CContext) {

  def this(csf: CSchemaFile, psi: SchemaValueTypeRef)(implicit ctx: CContext) = this(
    csf, CTypeRef(csf, psi.getTypeRef), CDataType.defaultTagName(psi.getDefaultOverride)
  )

  val name: String = typeRef.name.name + defaultTagNameDecl.map(" default " + _).getOrElse("")

  csf.dataTypes.add(this) // register self with schema file

  def defaultTag: Option[CTag] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _defaultTag)

  private lazy val _defaultTag: Option[CTag] = typeRef.resolved match {
    case t: CVarTypeDef => defaultTagNameDecl match {
      case Some(tagName) =>
        val tagOpt = t.effectiveTags.find(_.name == tagName)
        if (tagOpt.isEmpty) ctx.errors.add(
          CError(csf.filename, CErrorPosition.NA, s"Tag `$tagName` is not defined for union type `${t.name.name}`")
        )
        tagOpt
      case None => None
    }
    case t: CDatumType =>
      if (defaultTagNameDecl.nonEmpty) ctx.errors.add(
        CError(csf.filename, CErrorPosition.NA, s"Invalid default tag `${defaultTagNameDecl.get}` for datum type `${t.name.name}`")
      )
      Some(t.impliedTag)
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  def hasDefault: Boolean = ctx.after(CPhase.RESOLVE_TYPEREFS, false, defaultTag.nonEmpty)

  // `None` - nodefault, `Some(String)` - effective default tag name
  def effectiveDefaultTagName: Option[String] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, defaultTag.map(_.name))

  // valid after CPhase.COMPUTE_SUPERTYPES
  def compatibleWith(superDataType: CDataType): Boolean = superDataType.typeRef.resolved.isAssignableFrom(
    typeRef.resolved
  ) && (superDataType.effectiveDefaultTagName match {
    case None => true // super has no effective default tag
    case Some(tagName) => effectiveDefaultTagName match { // super has effective default tag
      case Some(`tagName`) => true // we have the same effective default tag as super
      case _ => false // we have either no default or some default different from super's
    }
  })

  def canEqual(other: Any): Boolean = other.isInstanceOf[CDataType]

  override def equals(other: Any): Boolean = other match {
    case that: CDataType => (that canEqual this) &&
          typeRef.name == that.typeRef.name &&
          defaultTagNameDecl == that.defaultTagNameDecl
    case _ => false
  }

  override def hashCode(): Int = name.hashCode

}

object CDataType {

  def defaultTagName(@Nullable psi: SchemaDefaultOverride): Option[String] =
    if (psi == null) None else Option(psi.getVarTagRef).map(_.getQid.getCanonicalName)

}
