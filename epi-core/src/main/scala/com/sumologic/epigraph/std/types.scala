/* Created by yegor on 5/12/16. */

package com.sumologic.epigraph.std

trait Type {

  type Super <: Type

  def name: QualifiedTypeName

  def declaredSupertypes: Seq[Super]

  def supertypes: Seq[Super] = cachedSupertypes

  private lazy val cachedSupertypes: Seq[Super] = declaredSupertypes.flatMap(_.supertypes.asInstanceOf).distinct

  def isAssignableFrom(sub: Type): Boolean = sub == this || sub.supertypes.contains(this)

}


trait MultiType[M <: MultiVar[M]] extends Type {

  override type Super <: MultiType[_ >: M]

  def declaredVarTags: Seq[VarTag[M, _]]

  def varTags: Seq[VarTag[_ >: M, _]] = cachedVarTags

  private lazy val cachedVarTags: Seq[VarTag[_ >: M, _]] = (declaredVarTags ++ supertypes.flatMap(_.varTags)).distinct

  def /*lazy val*/ listOf: ListType[Null, M] = ??? //TODO = new ListType[Null, M](name.listOf, supertypes.map(_.listOf), this)

}


abstract class MultiVarType[M <: MultiVar[M]](
    override final val name: QualifiedTypeName,
    override final val declaredSupertypes: Seq[MultiType[_ >: M]] = Nil
) extends MultiType[M] {

  final override type Super = MultiType[_ >: M]

}


trait DefaultMultiType[D <: Datum[D]] extends MultiType[D] {this: DataType[D] =>

  val default: VarTag[D, D] = new VarTag[D, D] { // TODO class

    override def name: TypeMemberName = TypeMemberName.default

    override def declaredDataType: DataType[D] = DefaultMultiType.this

  }

  override def declaredVarTags: Seq[VarTag[D, D]] = Seq[VarTag[D, D]](default)

  override def listOf: ListType[Null, D] = ???

}


trait DataType[D <: Datum[D]] extends Type with DefaultMultiType[D] {

  override type Super <: DataType[_ >: D]

  def isPolymorphic: Boolean

  def unapply(arg: Any): Option[D] = arg match {
    case datum: Datum[_] if isAssignableFrom(datum.dataType) => Some(datum.asInstanceOf[D])
    case _ => None
  }

}


abstract class RecordType[D <: RecordDatum[D]](
    override final val name: QualifiedTypeName,
    override final val declaredSupertypes: Seq[RecordType[_ >: D]] = Nil,
    override final val isPolymorphic: Boolean = false
) extends DataType[D] {

  final override type Super = RecordType[_ >: D]

  def declaredFields: Seq[Field[D, _]]

}


trait Field[D <: RecordDatum[D], M <: MultiVar[M]] {

  def valueType: MultiType[M]

  def as[T <: Datum[T]](varTag: VarTag[_ >: M, T]): TaggedField[D, M, varTag.type, T]

}


trait TaggedField[D <: RecordDatum[D], M <: MultiVar[M], Tag <: VarTag[_ >: M, T], T <: Datum[T]] extends Field[D, M]
    /*with Tagged[Tag, M, T]*/ {

  def tag: Tag

}


trait MapType[D <: MapDatum[D, K, M], K <: Datum[K], M <: MultiVar[M]] extends DataType[D] {

  final override type Super = MapType[_ >: D, K, _ >: M]

  def keyType: DataType[K]

  def valueType: MultiType[M]

}


trait ListType[D <: ListDatum[D, M], M <: MultiVar[M]] extends DataType[D] {

  final override type Super = ListType[_ >: D, _ >: M]

  def valueType: MultiType[M]

}


trait EnumType[D <: EnumDatum[D]] extends DataType[D] {

  final override type Super = EnumType[D]

  def values: Seq[D]

  override def isPolymorphic: Boolean = false

  override def declaredSupertypes: Seq[EnumType[D]] = Nil

}


trait PrimitiveType[D <: PrimitiveDatum[D]] extends DataType[D] {

  override type Super <: PrimitiveType[_ >: D]

  type Native

}


class StringType[D <: StringDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[StringType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = StringType[_ >: D]

  final override type Native = String

}


class IntegerType[D <: IntegerDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[IntegerType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = IntegerType[_ >: D]

  final override type Native = Int

}


class LongType[D <: LongDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[LongType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = LongType[_ >: D]

  final override type Native = Long

}


class DoubleType[D <: DoubleDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[DoubleType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = DoubleType[_ >: D]

  final override type Native = Double

}


class BooleanType[D <: BooleanDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[BooleanType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = BooleanType[_ >: D]

  final override type Native = Boolean

}
