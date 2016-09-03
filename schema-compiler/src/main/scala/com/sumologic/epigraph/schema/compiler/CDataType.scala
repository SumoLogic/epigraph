/* Created by yegor on 8/23/16. */

package com.sumologic.epigraph.schema.compiler

import io.epigraph.schema.parser.psi.{SchemaDefaultOverride, SchemaValueTypeRef}
import org.jetbrains.annotations.Nullable

/**
 * Container (field value, list element, or map value) data type.
 */
final class CDataType(
    val csf: CSchemaFile,
    val polymorphic: Boolean,
    val typeRef: CTypeRef,
    val defaultTagName: Option[String]
)(implicit val ctx: CContext) {

  def this(csf: CSchemaFile, psi: SchemaValueTypeRef)(implicit ctx: CContext) = this(
    csf, psi.getPolymorphic ne null, CTypeRef(csf, psi.getTypeRef), CDataType.defaultTagName(psi.getDefaultOverride)
  )

  @deprecated("use defaultTagName")
  val defaultDeclarationOpt: Option[Option[String]] = Option(defaultTagName)

  val name: String =
    (if (polymorphic) "polymorphic " else "") + typeRef.name.name + defaultTagName.map(" default " + _).getOrElse("")

  def canEqual(other: Any): Boolean = other.isInstanceOf[CDataType]

  override def equals(other: Any): Boolean = other match {
    case that: CDataType =>
      (that canEqual this) &&
          polymorphic == that.polymorphic &&
          typeRef.name == that.typeRef.name &&
          defaultTagName == that.defaultTagName
    case _ => false
  }

  override def hashCode(): Int = name.hashCode

}

object CDataType {

  def defaultTagName(@Nullable psi: SchemaDefaultOverride): Option[String] =
    if (psi == null) None else Option(psi.getVarTagRef).map(_.getQid.getCanonicalName)

}
