/* Created by yegor on 5/27/16. */

package com.sumologic.epigraph.xp.data

import com.sumologic.epigraph.names.{EnumValueName, TypeMemberName}
import com.sumologic.epigraph.xp.types._

import scala.util.Try

trait Var[+M <: Var[M]] {

  def getEntry[N <: TypeMemberName, T <: Datum[T]](tag: VarTag[_ >: M, N, T]): Option[VarEntry[_ <: M, N, T]]

}


trait MonoVar[+T <: Datum[T]] extends Var[T]


abstract class VarEntry[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](val tag: VarTag[M, N, T]) { // TODO variance?

  def value: Try[T]

}


trait Datum[+D <: Datum[D]] extends MonoVar[D] {

  type DatumType <: DataType[_ <: D]

  def dataType: DatumType

}


trait RecordDatum[+D <: RecordDatum[D]] extends Datum[D] {

  override type DatumType <: RecordType[_ <: D]

  def getData[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: TaggedField[_ >: D, M, N, T]
  ): Option[T] = getData[M, N, T](field, field.tag)

  def getData[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      varTag: VarTag[_ >: M, N, T]
  ): Option[T] = getValue[M, N, T](field, varTag).flatMap(_.toOption)

  // TODO getError([tagged]field[, tag])

  def getValue[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      varTag: VarTag[_ >: M, N, T]
  ): Option[Try[T]] = getVarEntry[M, N, T](field, varTag).map(_.value)

  def getVarEntry[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      varTag: VarTag[_ >: M, N, T]
  ): Option[VarEntry[_ <: M, N, T]] = getVar(field).flatMap(_.getEntry(varTag))

  def getVar[M <: Var[M]](field: Field[_ >: D, M]): Option[Var[M]]

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
