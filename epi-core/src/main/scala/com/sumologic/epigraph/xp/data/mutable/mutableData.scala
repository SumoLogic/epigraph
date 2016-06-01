/* Created by yegor on 5/27/16. */

package com.sumologic.epigraph.xp.data.mutable

import java.util.concurrent.ConcurrentHashMap

import com.sumologic.epigraph.names.{FieldName, TypeMemberName}
import com.sumologic.epigraph.xp.data._
import com.sumologic.epigraph.xp.types._

import scala.util.{Failure, Try}

trait MuVar[M <: Var[M]] extends Var[M] {

  override def getEntry[N <: TypeMemberName, TT <: Datum[TT]](tag: VarTag[_ >: M, N, TT]): Option[MuVarEntry[_ <: M, N, TT]]

  def getOrCreateEntry[N <: TypeMemberName, TT <: Datum[TT]](tag: VarTag[_ >: M, N, TT]): MuVarEntry[_ <: M, N, TT]

  final def setData[N <: TypeMemberName, TT <: Datum[TT]](
      tag: VarTag[_ >: M, N, TT],
      data: => TT
  ): this.type = setValue(tag, Try(data))

  final def setError[N <: TypeMemberName, TT <: Datum[TT]](
      tag: VarTag[_ >: M, N, TT],
      error: => Throwable
  ): this.type = setValue(tag, Failure(error))

  @throws[RuntimeException]("If super tag data type is wider than overridden tag")
  def setValue[N <: TypeMemberName, TT <: Datum[TT]](
      tag: VarTag[_ >: M, N, TT],
      value: => Try[TT]
  ): this.type

}


trait MuMonoVar[T <: Datum[T]] extends MonoVar[T] with MuVar[T] {this: MuDatum[T] =>

  val entry: MuVarEntry[T, TypeMemberName.default.type, T] = new MuVarEntry[T, TypeMemberName.default.type, T](
    dataType.default
  )

  private lazy val someEntry = Some(entry)

  override def getEntry[N <: TypeMemberName, TT <: Datum[TT]](
      tag: VarTag[_ >: T, N, TT]
  ): Option[MuVarEntry[_ <: T, N, TT]] = {
    checkReadTag(tag)
    someEntry.asInstanceOf[Option[MuVarEntry[T, N, TT]]] // TODO explain why cast is ok
  }

  override def getOrCreateEntry[N <: TypeMemberName, TT <: Datum[TT]](
      tag: VarTag[_ >: T, N, TT]
  ): MuVarEntry[_ <: T, N, TT] = getEntry(tag).get

  override def setValue[N <: TypeMemberName, TT <: Datum[TT]](
      tag: VarTag[_ >: T, N, TT],
      value: => Try[TT]
  ): MuMonoVar.this.type = {
    checkWriteTag(tag)
    entry.value = value.asInstanceOf[Try[T]] // checkWriteTag assures that `TT` is a subtype of `T`
    this
  }

  @throws[RuntimeException]("TODO cause")
  private def checkReadTag[N <: TypeMemberName, TT <: Datum[TT]](tag: VarTag[_ >: T, N, TT]): Unit = {
    if (!(tag.name == dataType.default.name && tag.dataType.isAssignableFrom(dataType))) {
      throw new RuntimeException // FIXME proper exception
    }
  }

  @throws[RuntimeException]("If super tag data type is wider than overridden tag")
  private def checkWriteTag[N <: TypeMemberName, TT <: Datum[TT]](tag: VarTag[_ >: T, N, TT]): Unit = {
    if (!(tag.name == dataType.default.name && dataType.isAssignableFrom(tag.dataType))) {
      throw new RuntimeException // FIXME proper exception
    }
  }

}


class MuMultiVar[M <: Var[M]] extends MuVar[M] {

  private val entries = new ConcurrentHashMap[TypeMemberName, MuVarEntry[_ <: M, _, _]]

  override def getEntry[N <: TypeMemberName, TT <: Datum[TT]](
      tag: VarTag[_ >: M, N, TT]
  ): Option[MuVarEntry[_ <: M, N, TT]] = Option[MuVarEntry[_ <: M, N, TT]](entries.get(tag.name).asInstanceOf[MuVarEntry[_ <: M, N, TT]])

  override def getOrCreateEntry[N <: TypeMemberName, TT <: Datum[TT]](
      tag: VarTag[_ >: M, N, TT]
  ): MuVarEntry[_ <: M, N, TT] = ???

  @throws[RuntimeException]("If super tag data type is wider than overridden tag")
  override def setValue[N <: TypeMemberName, TT <: Datum[TT]](
      tag: VarTag[_ >: M, N, TT],
      value: => Try[TT]
  ): MuMultiVar.this.type = ???

}


class MuVarEntry[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
    override val tag: VarTag[M, N, T],
    var value: Try[T] = MuVarEntry.Uninitialized
) extends VarEntry[M, N, T](tag) {

  def this(tag: VarTag[M, N, T], data: => T) = this(tag, Try(data))

  def this(tag: VarTag[M, N, T], error: Throwable) = this(tag, Failure(error))

  def setData(data: => T): this.type = {
    value = Try(data)
    this
  }

  def setError(error: Throwable): this.type = {
    value = Failure(error)
    this
  }

}


object MuVarEntry {

  val Uninitialized: Failure[Nothing] = Failure(UninitializedFieldError("Uninitialized var"))

}


trait MuDatum[D <: Datum[D]] extends MuMonoVar[D] with Datum[D] {

  override type DatumType <: DataType[D]

}


trait MuRecordDatum[D <: MuRecordDatum[D]] extends RecordDatum[D] with MuDatum[D] {

  override type DatumType = RecordType[D]

  private val entries = new ConcurrentHashMap[FieldName, MuVar[_]]

  override def getVar[M <: Var[M]](field: Field[_ >: D, M]): Option[MuVar[M]] = {
    Option(entries.get(field.name).asInstanceOf[MuVar[M]]) // TODO explain why ok
  }

  def setData[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: TaggedField[_ >: D, M, N, T],
      data: => T
  ): this.type = setData[M, N, T](field, field.tag, data)

  def setData[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      tag: VarTag[_ >: M, N, T],
      data: => T
  ): this.type = setValue[M, N, T](field, tag, Try(data))

  // TODO getError([tagged]field[, tag])

  def setValue[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      tag: VarTag[_ >: M, N, T],
      value: Try[T]
  ): this.type = {
    getOrCreateVarEntry[M, N, T](field, tag).value = value
    this
  }

  def getOrCreateVarEntry[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      tag: VarTag[_ >: M, N, T]
  ): MuVarEntry[M, N, T]

  def getOrCreateVar[M <: Var[M]](
      field: Field[_ >: D, M]
  ): MuVar[M] = ??? // entries.computeIfAbsent()getOrDefault()


}


trait MuMapDatum[K <: Datum[K], M <: Var[M]] extends MapDatum[K, M] with MuDatum[MapDatum[K, M]] {

  override type DatumType = MapType[K, M]

  def getVar(key: K): Option[M]

  //def getVarEntry[T <: Datum](key: K, varTag: VarTag[_ <: T]): Option[StaticVarEntry[_ >: M, T]]

}


trait MuTaggedMapDatum[K <: Datum[K], M <: Var[M], N <: TypeMemberName, T <: Datum[T]] extends TaggedMapDatum[K, M, N, T] with MuMapDatum[K, M] {

}


trait MuListDatum[M <: Var[M]] extends ListDatum[M] with MuDatum[ListDatum[M]] {

  override type DatumType = ListType[M]

}


trait MuTaggedListDatum[M <: Var[M], N <: TypeMemberName, V <: Datum[V]] extends TaggedListDatum[M, N, V] with MuListDatum[M] {

}

//trait MuEnumDatum[D <: MuEnumDatum[D]] extends EnumDatum[D] with MuDatum[D] {
//
//}

trait MuPrimitiveDatum[D <: MuPrimitiveDatum[D]] extends PrimitiveDatum[D] with MuDatum[D] {

  override type DatumType <: PrimitiveType[D]

  def set(native: PrimitiveDatum[D]#DatumType#Native) // TODO better type

}


trait MuStringDatum[D <: MuStringDatum[D]] extends StringDatum[D] with MuPrimitiveDatum[D] {

  override type DatumType = StringType[D]

}


trait MuIntegerDatum[D <: MuIntegerDatum[D]] extends IntegerDatum[D] with MuPrimitiveDatum[D] {

  override type DatumType = IntegerType[D]

}


trait MuLongDatum[D <: MuLongDatum[D]] extends LongDatum[D] with MuPrimitiveDatum[D] {

  override type DatumType = LongType[D]

}


trait MuDoubleDatum[D <: MuDoubleDatum[D]] extends DoubleDatum[D] with MuPrimitiveDatum[D] {

  override type DatumType = DoubleType[D]

}


trait MuBooleanDatum[D <: MuBooleanDatum[D]] extends BooleanDatum[D] with MuPrimitiveDatum[D] {

  override type DatumType = BooleanType[D]

}
