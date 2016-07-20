/* Created by yegor on 7/1/16. */

package com.sumologic.epigraph.schema.compiler

import com.sumologic.epigraph.schema.parser.psi.{SchemaAnonList, SchemaAnonMap, SchemaFqnTypeRef, SchemaTypeRef}


object CTypeRef {

  def apply(csf: CSchemaFile, psi: SchemaTypeRef)(implicit ctx: CContext): CTypeRef = psi match {
    case sftr: SchemaFqnTypeRef => apply(csf, sftr)
    case sal: SchemaAnonList => apply(csf, sal)
    case sam: SchemaAnonMap => apply(csf, sam)
    case _ => throw new RuntimeException // TODO exception
  }

  def apply(csf: CSchemaFile, psi: SchemaFqnTypeRef)(implicit ctx: CContext): CTypeDefRef =
    new CTypeDefRef(csf, psi)

  def apply(csf: CSchemaFile, psi: SchemaAnonList)(implicit ctx: CContext): CAnonListTypeRef =
    new CAnonListTypeRef(csf, psi)

  def apply(csf: CSchemaFile, psi: SchemaAnonMap)(implicit ctx: CContext): CAnonMapTypeRef =
    new CAnonMapTypeRef(csf, psi)

}

abstract class CTypeRef protected(val csf: CSchemaFile, val psi: SchemaTypeRef)(implicit val ctx: CContext) {

  type Name <: CTypeName

  type Type <: CType

  val name: Name

  @throws[java.util.NoSuchElementException]("If typeref hasn't been resolved")
  def resolved: Type = typeOptVar.get

  def isResolved: Boolean = typeOptVar.nonEmpty

  private var typeOptVar: Option[Type] = None

  csf.typerefs.add(this)

  def resolveTo(ctype: Type): this.type = {
    assert(typeOptVar.isEmpty, typeOptVar.get.name.name)
    assert(ctype.name == name)
    typeOptVar = Some(ctype)
    this
  }

}


class CTypeDefRef(csf: CSchemaFile, psi: SchemaFqnTypeRef)(implicit ctx: CContext) extends CTypeRef(csf, psi) {

  final override type Name = CTypeFqn

  final override type Type = CTypeDef

  override val name: CTypeFqn = csf.qualifyLocalTypeRef(psi)

}


class CAnonListTypeRef(csf: CSchemaFile, psi: SchemaAnonList)(implicit ctx: CContext) extends CTypeRef(csf, psi) {

  final override type Name = CAnonListTypeName

  final override type Type = CAnonListType

  override val name: CAnonListTypeName = new CAnonListTypeName(csf, psi)

}


class CAnonMapTypeRef(csf: CSchemaFile, psi: SchemaAnonMap)(implicit ctx: CContext) extends CTypeRef(csf, psi) {

  final override type Name = CAnonMapTypeName

  final override type Type = CAnonMapType

  override val name: CAnonMapTypeName = new CAnonMapTypeName(csf, psi)

}
