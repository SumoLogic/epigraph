/* Created by yegor on 7/1/16. */

package com.sumologic.epigraph.schema.compiler

import com.intellij.psi.PsiElement
import io.epigraph.lang.Qn
import io.epigraph.schema.parser.psi.{SchemaAnonList, SchemaAnonMap, SchemaQnTypeRef, SchemaTypeDef}
import org.jetbrains.annotations.Nullable


abstract class CTypeName protected(val name: String)(implicit val ctx: CContext) {

  final def canEqual(other: Any): Boolean = other.isInstanceOf[CTypeName]

  final override def equals(other: Any): Boolean = other match {
    case that: CTypeName => that.canEqual(this) && name == that.name
    case _ => false
  }

  final override def hashCode: Int = name.hashCode

}


class CTypeFqn private(csf: CSchemaFile, val fqn: Qn, val psi: PsiElement)(implicit ctx: CContext)
    extends CTypeName(fqn.toString) {

  val local: String = fqn.last()

  @Nullable val namespace: String = if (fqn.size == 1) null else fqn.removeLastSegment().toString

  def this(csf: CSchemaFile, parentNs: Qn, lqn: SchemaQnTypeRef)(implicit ctx: CContext) = this(
    csf, parentNs.append(lqn.getQn.getQn), lqn: PsiElement
  )

  def this(csf: CSchemaFile, parentNs: Qn, typeDef: SchemaTypeDef)(implicit ctx: CContext) = this(
    csf, parentNs.append(typeDef.getQid.getCanonicalName), typeDef.getQid.getId: PsiElement
  )

}


class CAnonListTypeName(val elementDataType: CDataType)(implicit ctx: CContext) extends {

  val elementTypeRef: CTypeRef = elementDataType.typeRef

} with CTypeName("list[" + elementDataType.name + "]")


class CAnonMapTypeName(val keyTypeRef: CTypeRef, val valueDataType: CDataType)(implicit ctx: CContext) extends {

  val valueTypeRef: CTypeRef = valueDataType.typeRef

} with CTypeName("map[" + keyTypeRef.name.name + "," + valueDataType.name + "]")
