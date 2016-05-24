/* Created by yegor on 5/20/16. */

package com.sumologic.epigraph.xp

import com.sumologic.epigraph.std

trait Var[+M <: Var[M]] {

  def getEntry[T <: Datum[T]](tag: VarTag[_ >: M, T]): Option[VarEntry[_ <: M, T]]

}


trait MonoVar[+T <: Datum[T]] extends Var[T] {

  //def getEntry[VT >: T <: Datum](tag: VarTag[VT]): Option[VarEntry[VT]]

}

//trait StaticVar[+M <: StaticVar[M]] extends Var {
//
//  def getEntry[T <: Datum](tag: StaticVarTag[_ >: M, T]): Option[VarEntry[T]]
//
//}

trait VarEntry[M <: Var[M], T <: Datum[T]] { // TODO variance?

  def tag: VarTag[M, T]

  def dataType: DataType[T]

  def data: T // TODO Option[T]? some other derivative?

  def error: Exception // TODO Throwable?

}


class VarTag[M <: Var[M], T <: Datum[T]](val name: std.TypeMemberName, val dataType: DataType[T])


trait Datum[+D <: Datum[D]] extends MonoVar[D] {

  type DatumType <: DataType[_]

  def dataType: DatumType

}


trait RecordDatum[+D <: RecordDatum[D]] extends Datum[D] {

  override type DatumType <: RecordType[_ <: D]

  def get[M <: Var[M], T <: Datum[T]](field: TaggedField[_ >: D, M, T]): T = {
    get[M, T](field, field.tag)
  }

  def get[M <: Var[M], T <: Datum[T]](field: Field[_ >: D, M], varTag: VarTag[_ >: M, T]): T

}

//trait MapDatum[K <: Datum[K], +M <: Var[M]] extends Datum {
//
//  override type DatumType <: MapType[K, _ <: M]
//
//  def get[T <: Datum](key: K, tag: VarTag[_ >: M, T]): Option[T] = ??? //getVar(key).flatMap(_.getEntry(tag))
//
//  def getOrElse[T <: Datum](key: K, tag: VarTag[_ >: M, _ <: T], default: => T): T
//
//  def getVar(key: K): Option[M]
//
//  //def getVarEntry[T <: Datum](key: K, varTag: VarTag[_ <: T]): Option[StaticVarEntry[_ >: M, T]]
//
//}

//trait StaticMapDatum[+D <: StaticMapDatum[D, K, M], K <: Datum, +M <: Var[M]] extends MapDatum[K, M] with Datum[D] {
//
//  override type DatumType <: StaticMapType[_ <: D, K, _ <: M]
//
//  def getOrElse[T <: Datum](key: K, tag: VarTag[_ >: M, _ <: T], default: => T): T
//
//  def getVar(key: K): Option[M]
//
//  //def getVarEntry[T <: Datum](key: K, varTag: StaticVarTag[_ >: M, _ <: T]): Option[StaticVarEntry[_ >: M, T]]
//
//}

//trait StaticTaggedMapDatum[+D <: StaticMapDatum[D, K, M], K <: Datum[K], M <: Var[M], V <: Datum]
//    extends StaticMapDatum[D, K, M] {
//
//  def tag: VarTag[_ >: M, V]
//
//}

trait ListDatum[+M <: Var[M]] extends Datum[ListDatum[M]] {

  override type DatumType <: ListType[_ <: M]

}


trait TaggedListDatum[M <: Var[M], V <: Datum[V]]
    extends ListDatum[M] {

  def tag: VarTag[_ >: M, V]

}


trait EnumDatum[D <: EnumDatum[D]] extends Datum[D] {

  override type DatumType <: EnumType[D]

  def name: std.EnumValueName

}


trait PrimitiveDatum[+D <: PrimitiveDatum[D]] extends Datum[D] {

  override type DatumType <: PrimitiveType[_ <: D]

  def native: DatumType#Native

}


trait StringDatum[+D <: StringDatum[D]] extends PrimitiveDatum[D] {

  override type DatumType <: StringType[_ <: D]

}


trait IntegerDatum[+D <: IntegerDatum[D]] extends PrimitiveDatum[D] {

  override type DatumType <: IntegerType[_ <: D]

}


trait LongDatum[+D <: LongDatum[D]] extends PrimitiveDatum[D] {

  override type DatumType <: LongType[_ <: D]

}


trait DoubleDatum[+D <: DoubleDatum[D]] extends PrimitiveDatum[D] {

  override type DatumType <: DoubleType[_ <: D]

}


trait BooleanDatum[+D <: BooleanDatum[D]] extends PrimitiveDatum[D] {

  override type DatumType <: BooleanType[_ <: D]

}


trait Type {

  type Super <: Type

  def name: std.QualifiedTypeName

  def declaredSupertypes: Seq[Super]

  def supertypes: Seq[Super] = cachedSupertypes

  private lazy val cachedSupertypes: Seq[Super] = declaredSupertypes.flatMap(_.supertypes.asInstanceOf).distinct

  def isAssignableFrom(sub: Type): Boolean = sub == this || sub.supertypes.contains(this)

}


trait MultiType[M <: Var[M]] extends Type {

  override type Super <: MultiType[_ >: M]

  def declaredVarTags: Seq[VarTag[M, _]]

  def varTags: Seq[VarTag[_ >: M, _]] = cachedVarTags

  private lazy val cachedVarTags: Seq[VarTag[_ >: M, _]] = (declaredVarTags ++ supertypes.flatMap(_.varTags)).distinct

  lazy val listOf: ListType[M] = new ListType[M](name.listOf, this, supertypes.map(_.listOf), false /* todo? */)

}


abstract class MultiVarType[M <: Var[M]](
    override final val name: std.QualifiedTypeName,
    override final val declaredSupertypes: Seq[MultiType[_ >: M]] = Nil
) extends MultiType[M] {

  final override type Super = MultiType[_ >: M]

  def declareTag[T <: Datum[T]](name: std.TypeMemberName, dataType: DataType[T]): VarTag[M, T] = new VarTag[M, T](
    name, dataType
  )

}


trait DefaultMultiType[D <: Datum[D]] extends MultiType[D] {this: DataType[D] =>

  val default: VarTag[D, D] = new VarTag[D, D](std.TypeMemberName.default, this)

  override def declaredVarTags: Seq[VarTag[D, D]] = Seq[VarTag[D, D]](default)

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
    override final val name: std.QualifiedTypeName,
    override final val declaredSupertypes: Seq[RecordType[_ >: D]] = Nil,
    override final val isPolymorphic: Boolean = false
) extends DataType[D] {

  final override type Super = RecordType[_ >: D]

  def declaredFields: Seq[Field[D, _]]

  protected def declareField[T <: Datum[T]](
      name: std.FieldName,
      valueType: DataType[T]
  ): TaggedField[D, T, T] = new TaggedField[D, T, T](name, valueType, valueType.default)

  protected def declareField[M <: Var[M]](name: std.FieldName, valueType: MultiType[M]): Field[D, M] = new Field[D, M](
    name, valueType
  )

  protected def declareField[M <: Var[M], T <: Datum[T]](
      name: std.FieldName,
      valueType: MultiType[M],
      tag: VarTag[_ >: M, T]
  ): TaggedField[D, M, T] = new TaggedField[D, M, T](name, valueType, tag)

}


class Field[D <: RecordDatum[D], M <: Var[M]](val name: std.FieldName, val valueType: MultiType[M]) {

  def as[T <: Datum[T]](varTag: VarTag[_ >: M, T]): TaggedField[D, M, T] = new TaggedField[D, M, T](
    name, valueType, varTag
  )

}


class TaggedField[D <: RecordDatum[D], M <: Var[M], T <: Datum[T]](
    override val name: std.FieldName,
    override val valueType: MultiType[M],
    val tag: VarTag[_ >: M, T]
) extends Field[D, M](name, valueType)

//trait MapType[K <: Datum, M <: Var[M]] extends DataType[MapDatum[K, M]] {
//
//  override type Super = MapType[K, _ >: M]
//
//  def keyType: DataType[K]
//
//  def valueType: MultiType[M]
//
//}
//
//
//// TODO class
//trait StaticMapType[D <: StaticMapDatum[D, K, M], K <: Datum, M <: Var[M]] extends MapType[K, M]// with StaticDataType[D]

class ListType[M <: Var[M]](
    override final val name: std.QualifiedTypeName,
    final val valueType: MultiType[M],
    override final val declaredSupertypes: Seq[ListType[_ >: M]] = Nil,
    override final val isPolymorphic: Boolean = false
) extends DataType[ListDatum[M]] {

  final override type Super = ListType[_ >: M]

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


abstract class StringType[D <: StringDatum[D]](
    override val name: std.QualifiedTypeName,
    override val declaredSupertypes: Seq[StringType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = StringType[_ >: D]

  final override type Native = String

}


class IntegerType[D <: IntegerDatum[D]](
    override val name: std.QualifiedTypeName,
    override val declaredSupertypes: Seq[IntegerType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = IntegerType[_ >: D]

  final override type Native = Int

}


class LongType[D <: LongDatum[D]](
    override val name: std.QualifiedTypeName,
    override val declaredSupertypes: Seq[LongType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = LongType[_ >: D]

  final override type Native = Long

}


class DoubleType[D <: DoubleDatum[D]](
    override val name: std.QualifiedTypeName,
    override val declaredSupertypes: Seq[DoubleType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = DoubleType[_ >: D]

  final override type Native = Double

}


class BooleanType[D <: BooleanDatum[D]](
    override val name: std.QualifiedTypeName,
    override val declaredSupertypes: Seq[BooleanType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = BooleanType[_ >: D]

  final override type Native = Boolean

}
