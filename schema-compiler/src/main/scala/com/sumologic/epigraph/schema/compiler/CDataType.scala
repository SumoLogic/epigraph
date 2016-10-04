/* Created by yegor on 8/23/16. */

package com.sumologic.epigraph.schema.compiler

import io.epigraph.schema.parser.psi.{SchemaDefaultOverride, SchemaValueTypeRef}
import org.jetbrains.annotations.Nullable

/**
 * Container (field value, list element, or map value) data type.
 */
final class CDataType( // TODO split into CVarDataType and CDatumDataType?
    val csf: CSchemaFile,
    @Deprecated val polymorphic: Boolean,
    val typeRef: CTypeRef,
    private val defaultTagNameDecl: Option[String]
)(implicit val ctx: CContext) {

  def this(csf: CSchemaFile, psi: SchemaValueTypeRef)(implicit ctx: CContext) = this(
    csf, false, CTypeRef(csf, psi.getTypeRef), CDataType.defaultTagName(psi.getDefaultOverride)
  )

  val name: String =
    (if (polymorphic) "polymorphic " else "") + typeRef.name.name + defaultTagNameDecl.map(" default " + _).getOrElse(
      ""
    )

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

  @deprecated("use defaultTagName")
  val defaultDeclarationOpt: Option[Option[String]] = Option(defaultTagNameDecl)

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
        polymorphic == that.polymorphic &&
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
