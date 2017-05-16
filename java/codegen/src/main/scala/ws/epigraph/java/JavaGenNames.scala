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

package ws.epigraph.java

import java.util.function.Function

import ws.epigraph.compiler._
import ws.epigraph.lang.Qn
import ws.epigraph.util.JavaNames

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
object JavaGenNames {
  /** java identifier name (https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.8) */
  def jn(name: String): String = JavaNames.javaName(name)

  /** name for a field constant within generated record type interface (basically checks for clash with `type` constant) */
  def fcn(f: CField): String = fcn(f.name)

  /** name for a field constant within generated record type interface (basically checks for clash with `type` constant) */
  def fcn(fname: String): String = javaFieldName(jn(fname))

  /** name for a tag constant within generated record type interface (basically checks for clash with `type` constant) */
  def tcn(f: CField): String = tcn(f.name)

  /** name for a tag constant within generated record type interface (basically checks for clash with `type` constant) */
  def tcn(fname: String): String = javaFieldName(jn(fname)) // todo javaTagName ?

  def javaFqn(fqn: Qn): String = fqn.segments.map(jn).mkString(".")

  /** java type name for given type as seen from the context of the other type namespace */
  def lqn(t: CType, lt: CType): String = lqn(t, lt, identity[String] _)

  /** java type name for given type as seen from the context of the other type namespace */
  def lqn(t: CType, lt: CType, lnTrans: (String) => String): String = lqn2(t, pn(lt), lnTrans)

  /** java type name for given type as seen from the context of the other type namespace */
  def lqn2(t: CType, namespace: String, lnTrans: (String) => String = identity): String = {
    val tpn = pn(t)
    if (tpn == namespace) lnTrans(ln(t)) else tpn + "." + lnTrans(ln(t))
  }

  def lqn(prefix: String, t: CType, lt: CType): String = lqn(t, lt, prefix + _)

  /** java package name for given type */
  def pn(t: CType): String = pn(getNamedTypeComponent(t).name.fqn.removeLastSegment())

  def pn(qn: Qn): String = qn.segments.map(jn).mkString(".").toLowerCase

  def pnq(qn: Qn): Qn = qn.map(new Function[String, String] {
    override def apply(t: String): String = jn(t).toLowerCase
  })

  /** escaped (in case it conflicts with reserved) local (short) java name for given epigraph type */
  def ln(t: CType): String = javaTypeName(ln0(t))

  /** local (short) java name for given epigraph type (not escaped) */
  private def ln0(t: CType): String = t match {
    case t: CTypeDef => t.name.local
    case t: CAnonListType => t.elementDataType.typeRef.resolved match {
      case et: CVarTypeDef => ln0(et) + varTagPart(t.elementDataType.effectiveDefaultTagName) + "_List"
      case et: CDatumType => ln0(et) + "_List"
      case unknown => throw new UnsupportedOperationException(unknown.toString)
    }
    case t: CAnonMapType => t.valueDataType.typeRef.resolved match {
      case vt: CVarTypeDef => ln0(t.keyTypeRef.resolved) + "_" + ln0(vt) + varTagPart(t.valueDataType.effectiveDefaultTagName) + "_Map"
      case vt: CDatumType => ln0(t.keyTypeRef.resolved) + "_" + ln0(vt) + "_Map"
      case unknown => throw new UnsupportedOperationException(unknown.toString)
    }
  }

  /** java type name for given typeref as seen from the context of the other type namespace */
  def lqrn(tr: CTypeRef, lt: CType, lnTrans: (String) => String = identity): String = lqn(tr.resolved, lt, lnTrans)

  /** locally qualified name for type's Data type (e.g. `PersonRecord.Data` or `Person`) */
  def lqdrn(tr: CTypeRef, lt: CType): String = tr.resolved match {
    case t: CVarTypeDef => lqn(t, lt)
    case t: CDatumType => lqn(t, lt) + ".Data"
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  /** locally qualified name for type's Data type (e.g. `PersonRecord.Data` or `Person`) */
  def lqdrn2(t: CType, namespace: String): String = t match {
    case t: CVarTypeDef => lqn2(t, namespace)
    case t: CDatumType => lqn2(t, namespace) + ".Data"
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  /** locally qualified name for type's Data Builder type (e.g. `PersonRecord.Data.Builder` or `Person.Builder`) */
  def lqbrn(t: CType, namespace: String): String = t match {
    case t: CVarTypeDef => lqn2(t, namespace) + ".Builder"
    case t: CDatumType => lqn2(t, namespace) + ".Data.Builder"
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  /** locally qualified constructor call for type's Data Builder type (e.g. `PersonRecord.type.createDataBuilder()` or `Person.create()`) */
  def lqbct(t: CType, namespace: String): String = t match {
    case t: CVarTypeDef => lqn2(t, namespace) + ".create()"
    case t: CDatumType => lqn2(t, namespace) + ".type.createDataBuilder()"
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  /** tag type for given typeref and tag name */
  def tt(tr: CTypeRef, tn: String): CType = tr.resolved match {
    case tt: CVarTypeDef => tt.effectiveTags.find(_.name == tn).get.typeRef.resolved
    case tt: CDatumType => tt
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  /** tag constant reference for given type and its tag name */
  def ttr(t: CType, tn: String, lt: CType): String = ttr(t, tn, pn(lt))

  /** tag constant reference for given type and its tag name */
  def ttr(t: CType, tn: String, namespace: String): String = t match {
    case t: CVarTypeDef => lqn2(t, namespace) + "." + jn(tn)
    case t: CDatumType => lqn2(t, namespace) + ".Type.instance().self"
    case unknown => throw new UnsupportedOperationException(unknown.name.name)
  }

  /** tag constant reference name for given data type and a tag (as seen from the context of the local type namespace) */
  def dttr(dt: CDataType, tn: String, lt: CType): String = ttr(dt.typeRef.resolved, tn, lt)

  /** default tag constant reference for given data type (as seen from the context of the local type namespace) */
  def tcr(dt: CDataType, lt: CType): String = dt.effectiveDefaultTagName match {
    case Some(tagName) => ttr(dt.typeRef.resolved, tagName, lt)
    case None => "null"
  }


  private def varTagPart(tagName: Option[String]): String = tagName match {
    case Some(name) => "$" + name
    case None => ""
  }

  def getNamedTypeComponent(t: CType): CTypeDef = t match {
    case td: CTypeDef => td
    case alt: CAnonListType => getNamedTypeComponent(alt.elementTypeRef.resolved)
    case amt: CAnonMapType => getNamedTypeComponent(amt.valueTypeRef.resolved)
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  def javaLocalName(t: CType, trans: (String) => String = identity): String = jn(trans(ln(t)))

  def javaQName(t: CType, ht: CType, trans: (String) => String = identity): String = JavaGenNames.javaFqn(
    localQName(getNamedTypeComponent(t).name.fqn, getNamedTypeComponent(ht).name.fqn.removeLastSegment(), trans)
  )

  def localQName(typeFqn: Qn, localNs: Qn, trans: (String) => String = identity): Qn = {
    val transLocal = trans(typeFqn.last())
    // FIXME use ln(t) here
    val ns = typeFqn.removeLastSegment()
    if (ns == localNs) new Qn(transLocal) else ns.append(transLocal)
  }

  def qnameArgs(fqn: Qn): Seq[String] = fqn.last() +: fqn.removeLastSegment().segments.toSeq

  def javaTypeName(ln: String): String = if (ReservedTypeNames.contains(ln)) ln + '_' else ln

  def javaFieldName(fn: String): String = if (ReservedFieldNames.contains(fn)) fn + '_' else fn

//  def objName(s: String): String = s
//
//  def objName(t: CTypeDef): String = javaLocalName(t, objName)

//  def baseName(s: String): String = s

//  @deprecated("use ln")
//  def baseName(t: CType): String = javaLocalName(t, baseName)
//
//  def baseQName(t: CType, ht: CType): String = javaQName(t, ht, baseName)

//  def baseValueName(s: String): String = s + ".Value"

//  def baseValueName(t: CTypeDef): String = javaLocalName(t, baseValueName)

//  def baseDataName(s: String): String = s + ".Data"

//  def baseDataName(t: CTypeDef): String = javaLocalName(t, baseDataName)

//  def elementName(t: CType): String = t match {
//    case alt: CAnonListType =>
//      elementName(alt.elementTypeRef.resolved) + ".List"
//    case amt: CAnonMapType =>
//      elementName(amt.valueTypeRef.resolved) + ".Map<" + elementName(amt.keyTypeRef.resolved) + ".Imm>"
//    case td: CTypeDef =>
//      ln(td)
//    case unknown => throw new UnsupportedOperationException(unknown.toString)
//  }

//  def typeComponents(t: CType): Seq[String] = t match {
//    case alt: CAnonListType => typeComponents(alt.elementTypeRef.resolved) :+ "List"
//    case amt: CAnonMapType => typeComponents(amt.valueTypeRef.resolved) :+ "Map"
//    case td: CTypeDef => Seq(ln(td))
//    case unknown => throw new UnsupportedOperationException(unknown.toString)
//  }

//  def immName(s: String): String = s + ".Imm"
//
//  def immName(t: CTypeDef): String = javaLocalName(t, immName)

//  def immQName(t: CTypeDef, ht: CTypeDef): String = javaQName(t, ht, immName)
//
//  @Deprecated
//  def mutName(s: String): String = s + ".Builder"
//
//  @Deprecated
//  def mutName(t: CTypeDef): String = javaLocalName(t, mutName)
//
//  @Deprecated
//  def mutImplName(s: String): String = mutName(s) + ".Impl"
//
//  @Deprecated
//  def mutImplName(t: CTypeDef): String = javaLocalName(t, mutImplName)
//
//  @Deprecated
/// def mutQName(t: CTypeDef, ht: CTypeDef): String = javaQName(t, ht, mutName)
//
//  def bldName(s: String): String = s + ".Builder"
//
//  def bldName(t: CTypeDef): String = jn(bldName(ln(t)))
//
//  def bldImplName(s: String): String = bldName(s)// + ".Impl"
//
//  def bldImplName(t: CTypeDef): String = javaLocalName(t, bldImplName)
//
//  def bldQName(t: CTypeDef, ht: CTypeDef): String = javaQName(t, ht, bldName)

//  def parentNames(t: CType, trans: (String) => String = identity): String =
//    t.getLinearizedParentsReversed.map(javaQName(_, t, trans)).mkString(", ")

  /** set of type names that conflict with our own generated java classes */
  private val ReservedTypeNames: Set[String] = Set("Type", "Value", "Data", "Imm", "Builder", "Impl", "Tag", "Field")

  private val ReservedFieldNames: Set[String] = Set("type")

}
