/* Created by yegor on 7/1/16. */

package ws.epigraph.schema.compiler

import ws.epigraph.schema.parser.psi.{SchemaAnonList, SchemaAnonMap, SchemaQnTypeRef, SchemaTypeRef}


object CTypeRef {

  def apply(csf: CSchemaFile, psi: SchemaTypeRef)(implicit ctx: CContext): CTypeRef = psi match {
    case psi: SchemaQnTypeRef => apply(csf, psi)
    case psi: SchemaAnonList => apply(csf, psi)
    case psi: SchemaAnonMap => apply(csf, psi)
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  def apply(csf: CSchemaFile, psi: SchemaQnTypeRef)(implicit ctx: CContext): CTypeDefRef =
    new CTypeDefRef(csf, psi)

  def apply(csf: CSchemaFile, psi: SchemaAnonList)(implicit ctx: CContext): CAnonListTypeRef =
    new CAnonListTypeRef(csf, psi)

  def apply(csf: CSchemaFile, psi: SchemaAnonMap)(implicit ctx: CContext): CAnonMapTypeRef =
    new CAnonMapTypeRef(csf, psi)

  def apply(ctype: CType)(implicit ctx: CContext): CTypeRef = ctype match {
    case ctype: CTypeDef => new CTypeDefRef(ctype.csf, ctype.name).resolveTo(ctype)
    case ctype: CAnonListType => new CAnonListTypeRef(ctype.elementDataType).resolveTo(ctype)
    case ctype: CAnonMapType => new CAnonMapTypeRef(ctype.keyTypeRef, ctype.valueDataType).resolveTo(ctype)
  }

}

abstract class CTypeRef protected(val csf: CSchemaFile)(implicit val ctx: CContext) {

  type Name <: CTypeName

  type Type <: CType

  val name: Name

  @throws[java.util.NoSuchElementException]("If typeref hasn't been resolved")
  def resolved: Type = typeOptVar.get

  def isResolved: Boolean = typeOptVar.nonEmpty

  private var typeOptVar: Option[Type] = None

  csf.typerefs.add(this)

  def resolveTo(ctype: Type): this.type = {
    typeOptVar.foreach(resolved => assert(resolved eq ctype, typeOptVar.get.name.name))
    assert(ctype.name == name)
    typeOptVar = Some(ctype)
    this
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[CTypeRef]

  override def equals(other: Any): Boolean = other match {
    case that: CTypeRef => (that canEqual this) && name == that.name
    case _ => false
  }

  override def hashCode(): Int = name.hashCode

}


class CTypeDefRef(csf: CSchemaFile, override val name: CTypeFqn)(implicit ctx: CContext) extends CTypeRef(csf) {

  def this(csf: CSchemaFile, psi: SchemaQnTypeRef)(implicit ctx: CContext) = this(csf, csf.qualifyLocalTypeRef(psi))

  final override type Name = CTypeFqn

  final override type Type = CTypeDef

}


class CAnonListTypeRef(val elementDataType: CDataType)(implicit ctx: CContext) extends CTypeRef(elementDataType.csf) {

  def this(csf: CSchemaFile, psi: SchemaAnonList)(implicit ctx: CContext) =
    this(new CDataType(csf, psi.getValueTypeRef))

  final override type Name = CAnonListTypeName

  final override type Type = CAnonListType

  final override val name: CAnonListTypeName = new CAnonListTypeName(elementDataType)

}


class CAnonMapTypeRef(val keyTypeRef: CTypeRef, val valueDataType: CDataType)(implicit ctx: CContext)
    extends CTypeRef(valueDataType.csf) {

  def this(csf: CSchemaFile, psi: SchemaAnonMap)(implicit ctx: CContext) =
    this(CTypeRef(csf, psi.getTypeRef), new CDataType(csf, psi.getValueTypeRef))

  final override type Name = CAnonMapTypeName

  final override type Type = CAnonMapType

  override val name: CAnonMapTypeName = new CAnonMapTypeName(keyTypeRef, valueDataType)

}
