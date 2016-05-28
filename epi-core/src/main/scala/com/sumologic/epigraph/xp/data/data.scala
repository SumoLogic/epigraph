/* Created by yegor on 5/27/16. */

package com.sumologic.epigraph.xp.data

import com.sumologic.epigraph.names.{EnumValueName, TypeMemberName}
import com.sumologic.epigraph.xp.types._

trait Var[+M <: Var[M]] {

  def getEntry[N <: TypeMemberName, T <: Datum[T]](tag: VarTag[_ >: M, N, T]): Option[VarEntry[_ <: M, N, T]]

}


trait MonoVar[+T <: Datum[T]] extends Var[T] {

  //def getEntry[VT >: T <: Datum](tag: VarTag[VT]): Option[VarEntry[VT]]

}


trait VarEntry[M <: Var[M], N <: TypeMemberName, T <: Datum[T]] { // TODO variance?

  def tag: VarTag[M, N, T]

  def dataType: DataType[T]

  def data: T // TODO Option[T]? some other derivative?

  def error: Exception // TODO Throwable?

}


trait Datum[+D <: Datum[D]] extends MonoVar[D] {

  type DatumType <: DataType[_]

  def dataType: DatumType

}


trait RecordDatum[+D <: RecordDatum[D]] extends Datum[D] {

  override type DatumType <: RecordType[_ <: D]

  def get[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](field: TaggedField[_ >: D, M, N, T]): T = {
    get[M, N, T](field, field.tag)
  }

  def get[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](field: Field[_ >: D, M], varTag: VarTag[_ >: M, N, T]): T

}


trait MapDatum[K <: Datum[K], +M <: Var[M]] extends Datum[MapDatum[K, M]] {

  override type DatumType <: MapType[K, _ <: M]

  def get[N <: TypeMemberName, T <: Datum[T]](
      key: K,
      tag: VarTag[_ >: M, N, T]
  ): Option[T] = ??? //getVar(key).flatMap(_.getEntry(tag))

  def getOrElse[N <: TypeMemberName, T <: Datum[T]](key: K, tag: VarTag[_ >: M, N, _ <: T], default: => T): T

  def getVar(key: K): Option[M]

  //def getVarEntry[T <: Datum](key: K, varTag: VarTag[_ <: T]): Option[StaticVarEntry[_ >: M, T]]

}


trait TaggedMapDatum[K <: Datum[K], /*+*/ M <: Var[M], N <: TypeMemberName, T <: Datum[T]] extends MapDatum[K, M] {

  def tag: VarTag[_ >: M, N, T]

}


trait ListDatum[+M <: Var[M]] extends Datum[ListDatum[M]] {

  override type DatumType <: ListType[_ <: M]

  def head[N <: TypeMemberName, T <: Datum[T]](tag: VarTag[_ >: M, N, T]): T // TODO Option[T] etc.?

}


trait TaggedListDatum[+M <: Var[M], N <: TypeMemberName, +V <: Datum[V]] extends ListDatum[M] {

  def tag: VarTag[_ <: M, N, _ <: V] // FIXME `_ <: M` is not good...

  def head: V = head(tag.asInstanceOf) // FIXME this .asInstanceOf is fishy - need to provide justification why it's ok

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
