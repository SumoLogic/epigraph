/* Created by yegor on 7/1/16. */

package com.sumologic.epigraph.schema.compiler

import com.intellij.psi.PsiElement
import io.epigraph.lang.parser.Fqn
import io.epigraph.lang.parser.psi.{EpigraphAnonList, EpigraphAnonMap, EpigraphFqnTypeRef, EpigraphTypeDef}
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._


abstract class CTypeName protected(val csf: CSchemaFile, val name: String, val psi: PsiElement)
    (implicit val ctx: CContext) {

  def canEqual(other: Any): Boolean = other.isInstanceOf[CTypeName]

  override def equals(other: Any): Boolean = other match {
    case that: CTypeName => that.canEqual(this) && name == that.name
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(name)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

}


class CTypeFqn private(csf: CSchemaFile, val fqn: Fqn, psi: PsiElement)(implicit ctx: CContext) extends CTypeName(
  csf, fqn.toString, psi
) {

  @scala.beans.BeanProperty
  val local: String = fqn.last()

  @scala.beans.BeanProperty
  @Nullable
  val namespace: String = if (fqn.size == 1) null else fqn.removeLastSegment().toString

  def this(csf: CSchemaFile, parentNs: Fqn, lqn: EpigraphFqnTypeRef)(implicit ctx: CContext) = this(
    csf, parentNs.append(lqn.getFqn.getFqn), lqn: PsiElement
  )

  def this(csf: CSchemaFile, parentNs: Fqn, typeDef: EpigraphTypeDef)(implicit ctx: CContext) = this(
    csf, parentNs.append(typeDef.getQid.getCanonicalName), typeDef.getQid.getId: PsiElement
  )

}


class CAnonListTypeName(csf: CSchemaFile, override val psi: EpigraphAnonList)(implicit ctx: CContext) extends {

  val elementValueType: CValueType = new CValueType(csf, psi.getValueTypeRef)

  val elementTypeRef: CTypeRef = elementValueType.typeRef

} with CTypeName(csf, CAnonListTypeName.anonListTypeName(elementTypeRef.name.name), psi)

object CAnonListTypeName {

  def anonListTypeName(elementTypeName: String): String = "list[" + elementTypeName + "]" // FIXME poly and default

}


class CAnonMapTypeName(csf: CSchemaFile, override val psi: EpigraphAnonMap)(implicit ctx: CContext) extends {

  val keyTypeRef: CTypeRef = CTypeRef(csf, psi.getTypeRef)

  val valueValueType: CValueType = new CValueType(csf, psi.getValueTypeRef)

  val valueTypeRef: CTypeRef = valueValueType.typeRef

} with CTypeName(csf, "map[" + keyTypeRef.name.name + "," + valueTypeRef.name.name + "]", psi)
