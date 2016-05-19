/* Created by yegor on 5/12/16. */

package com.sumologic.epigraph.std

trait MultiVar[+M <: MultiVar[M]] {

  def get[T <: Datum[T]](tag: VarTag[_ >: M, T]): Option[T]

}


trait MonoVar[+D <: Datum[D]] extends MultiVar[D] { //this: D =>

}


trait VarEntry[M <: MultiVar[M], T <: Datum[T]] {

  def tag: VarTag[M, T]

  def dataType: DataType[T]

  def data: T // TODO Option[T]? some other derivative?

  def error: Exception // TODO Throwable?

}


trait VarTag[M <: MultiVar[M], T <: Datum[T]] {

  def name: TypeMemberName

  def declaredDataType: DataType[T]

}

//trait Tagged[Tag <: VarTag[_ >: M, T], M <: MultiVar[M], T <: Datum[T]] {
//
//  def tag: Tag
//
//}

trait Datum[+D <: Datum[D]] extends MonoVar[D] {

  type DatumType <: DataType[_ <: D]

  def dataType: DatumType

}


trait RecordDatum[+D <: RecordDatum[D]] extends Datum[D] {

  override type DatumType <: RecordType[_ <: D]

  def get[M <: MultiVar[M], Tag <: VarTag[_ >: M, T], T <: Datum[T]](field: TaggedField[_ >: D, M, Tag, T]): T = {
    get[M, T](field, field.tag)
  }

  def get[M <: MultiVar[M], T <: Datum[T]](field: Field[_ >: D, M], varTag: VarTag[_ >: M, T]): T

}


trait Field[D <: RecordDatum[D], M <: MultiVar[M]] {

  def valueType: MultiType[M]

  def as[T <: Datum[T]](varTag: VarTag[_ >: M, T]): TaggedField[D, M, varTag.type, T]

}


trait TaggedField[D <: RecordDatum[D], M <: MultiVar[M], Tag <: VarTag[_ >: M, T], T <: Datum[T]] extends Field[D, M]
    /*with Tagged[Tag, M, T]*/ {

  def tag: Tag

}


trait MapDatum[+D <: MapDatum[D, K, M], K <: Datum[K], +M <: MultiVar[M]] extends Datum[D] {

  override type DatumType <: MapType[_ <: D, K, _ <: M]

  def get[T <: Datum[T]](key: K, tag: VarTag[_ >: M, T]): Option[T] = getVar(key).flatMap(_.get(tag))

  def getOrElse[T <: Datum[T]](key: K, tag: VarTag[_ >: M, _ <: T], default: => T): T

  def getVar(key: K): Option[MultiVar[M]]

}


trait TaggedMapDatum[+D <: MapDatum[D, K, M], K <: Datum[K], M <: MultiVar[M], Tag <: VarTag[_ >: M, V], V <: Datum[V]]
    extends MapDatum[D, K, M] {

  def tag: Tag

}


trait ListDatum[+D <: ListDatum[D, M], +M <: MultiVar[M]] extends Datum[D] {

  override type DatumType <: ListType[_ <: D, _ <: M]

}


trait TaggedListDatum[+D <: ListDatum[D, M], M <: MultiVar[M], Tag <: VarTag[_ >: M, V], V <: Datum[V]]
    extends ListDatum[D, M] {

  def tag: Tag

}


trait EnumDatum[D <: EnumDatum[D]] extends Datum[D] {

  override type DatumType <: EnumType[D]

  def name: EnumValueName

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
