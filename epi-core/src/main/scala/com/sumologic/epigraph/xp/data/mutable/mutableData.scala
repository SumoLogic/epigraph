/* Created by yegor on 5/27/16. */

package com.sumologic.epigraph.xp.data.mutable

import java.util.concurrent.ConcurrentHashMap

import com.sumologic.epigraph.names.{FieldName, TypeMemberName}
import com.sumologic.epigraph.xp.data._
import com.sumologic.epigraph.xp.types._

import scala.util.{Failure, Try}

trait MutVar[M <: Var[M]] extends Var[M] { // TODO take/require vartype?

  override def getEntry[TT <: Datum[TT]](tag: Tag[_ >: M, _, TT]): Option[VarEntry[TT]]

  def getEntry[TT <: Datum[TT]](tag: FinalTag[_ >: M, _, TT]): Option[MutVarEntry[TT]]

  def getOrCreateEntry[TT <: Datum[TT]](tag: FinalTag[_ >: M, _ <: TypeMemberName, TT]): MutVarEntry[TT]

  final def setData[TT <: Datum[TT]](tag: FinalTag[_ >: M, _, TT], data: => TT): this.type = setValue(tag, Try(data))

  final def setError[TT <: Datum[TT]](tag: FinalTag[_ >: M, _, TT], error: => Throwable): this.type = setValue(
    tag, Failure(error)
  )

  def setValue[TT <: Datum[TT]](tag: FinalTag[_ >: M, _, TT], value: => Try[TT]): this.type

}

//trait MuMonoVar[T <: Datum[T]] extends MonoVar[T] with MuVar[T] {this: MuDatum[T] =>
//
//  val entry: MuVarEntry[T] = new MuVarEntry[T](dataType.default)
//
//  private lazy val someEntry = Some(entry)
//
//  override def getEntry[TT <: Datum[TT]](
//      tag: Tag[_ >: T, _, TT]
//  ): Option[MuVarEntry[TT]] = {
//    checkReadTag(tag)
//    someEntry.asInstanceOf[Option[MuVarEntry[TT]]] // TODO explain why cast is ok
//  }
//
//  override def getOrCreateEntry[TT <: Datum[TT]](
//      tag: Tag[_ >: T, _, TT]
//  ): MuVarEntry[TT] = getEntry(tag).get
//
//  override def setValue[TT <: Datum[TT]](
//      tag: Tag[_ >: T, _, TT],
//      value: => Try[TT]
//  ): this.type = {
//    checkWriteTag(tag)
//    entry.value = value.asInstanceOf[Try[T]] // checkWriteTag assures that `TT` is a subtype of `T`
//    this
//  }
//
//  @throws[RuntimeException]("TODO cause")
//  private def checkReadTag[TT <: Datum[TT]](tag: Tag[_ >: T, _, TT]): Unit = {
//    if (!(tag.name == dataType.default.name && tag.dataType.isAssignableFrom(dataType))) {
//      throw new RuntimeException // FIXME proper exception
//    }
//  }
//
//  @throws[RuntimeException]("If super tag data type is wider than overridden tag")
//  private def checkWriteTag[TT <: Datum[TT]](tag: Tag[_ >: T, _, TT]): Unit = {
//    if (!(tag.name == dataType.default.name && dataType.isAssignableFrom(tag.dataType))) {
//      throw new RuntimeException // FIXME proper exception
//    }
//  }
//
//}

class MutMultiVar[M <: Var[M]] extends MutVar[M] {

  private val entries = new ConcurrentHashMap[TypeMemberName, MutVarEntry[_]]

  override def getEntry[TT <: Datum[TT]](tag: Tag[_ >: M, _, TT]): Option[VarEntry[TT]] = Option[VarEntry[TT]](
    entries.get(tag.name).asInstanceOf[VarEntry[TT]] // TODO explain cast
  )

  override def getEntry[TT <: Datum[TT]](tag: FinalTag[_ >: M, _, TT]): Option[MutVarEntry[TT]] = Option[MutVarEntry[TT]](
    entries.get(tag.name).asInstanceOf[MutVarEntry[TT]] // TODO explain cast
  )

  override def getOrCreateEntry[TT <: Datum[TT]](tag: FinalTag[_ >: M, _ <: TypeMemberName, TT]): MutVarEntry[TT] = {
    // TODO find most narrow tag (two different inherited once must've been merged into local or failed in compile)
    entries.computeIfAbsent(tag.name, new MuVarEntryConstructor/*(tag)*/).asInstanceOf[MutVarEntry[TT]]
  }


  // TODO: take this from the tag itself (and lazy there)?
  private class MuVarEntryConstructor[TT <: Datum[TT]]/*(private val tag: FinalTag[_, _, TT])*/
      extends java.util.function.Function[TypeMemberName, MutVarEntry[TT]] {

    override def apply(t: TypeMemberName): MutVarEntry[TT] = new MutVarEntry[TT]/*(tag)*/

  }


  @throws[RuntimeException]("If super tag data type is wider than overridden tag")
  override def setValue[TT <: Datum[TT]](
      tag: FinalTag[_ >: M, _, TT],
      value: => Try[TT]
  ): this.type = ???

}


class MutVarEntry[T <: Datum[T]](
    /*override val tag: VarTag[_, _, T],*/
    var value: Try[T] = MutVarEntry.Uninitialized
) extends VarEntry[T] {

  def this(/*tag: FinalTag[_, _, T], */ data: => T) = this(/*tag, */ Try(data))

  def this(/*tag: FinalTag[_, _, T], */ error: Throwable) = this(/*tag, */ Failure(error))

  def setData(data: => T): this.type = {
    value = Try(data)
    this
  }

  def setError(error: Throwable): this.type = {
    value = Failure(error)
    this
  }

}


object MutVarEntry {

  val Uninitialized: Failure[Nothing] = Failure(UninitializedFieldError("Uninitialized var"))

}


trait MutDatum[+D <: Datum[D]] extends Datum[D] {

  //override type DatumType <: DataType[D]

}


trait MutRecordDatum[+D <: RecordDatum[D]] extends RecordDatum[D] with MutDatum[D] {

  //override type DatumType = RecordType[D]

  private val vars = new ConcurrentHashMap[FieldName, MutVar[_]]

  override def getVar[M <: Var[M]](field: Field[_ >: D, M]): Option[MutVar[M]] = {
    Option(vars.get(field.name).asInstanceOf[MutVar[M]]) // TODO explain why ok
  }

  def setData[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: TaggedField[_ >: D, M, N, T], // TODO FinTaggedField?
      data: => T
  ): this.type = ??? // setData[M, N, T](field, field.tag, data)

  def setData[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      tag: FinalTag[_ >: M, N, T],
      data: => T
  ): this.type = setValue[M, N, T](field, tag, Try(data))

  // TODO getError([tagged]field[, tag])

  def setValue[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      tag: FinalTag[_ >: M, N, T],
      value: Try[T]
  ): this.type = {
    getOrCreateVarEntry[M, N, T](field, tag).value = value
    this
  }

  def getOrCreateVarEntry[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      tag: FinalTag[_ >: M, N, T]
  ): MutVarEntry[T] = getOrCreateVar(field).getOrCreateEntry(tag)

  def getOrCreateVar[M <: Var[M]](field: Field[_ >: D, M]): MutVar[M] = {
    vars.computeIfAbsent(field.name, new MuVarConstructor/*(field)*/).asInstanceOf[MutVar[M]]
  }


  // TODO: take this from the field itself (and lazy there)? convert to an object?
  private class MuVarConstructor[M <: Var[M]]/*(private val field: Field[_ >: D, M])*/
      extends java.util.function.Function[FieldName, MutVar[M]] {

    override def apply(t: FieldName): MutVar[M] = new MutMultiVar[M]

  }


}


trait MutMapDatum[K <: Datum[K], +M <: Var[M]] extends MapDatum[K, M] with MutDatum[MapDatum[K, M]] {

  //override type DatumType = MapType[K, M]

  def getVar(key: K): Option[M]

  //def getVarEntry[T <: Datum](key: K, varTag: VarTag[_ <: T]): Option[StaticVarEntry[_ >: M, T]]

}


trait MutTaggedMapDatum[K <: Datum[K], /*+*/ M <: Var[M], N <: TypeMemberName, T <: Datum[T]]
    extends TaggedMapDatum[K, M, N, T] with MutMapDatum[K, M]


trait MutListDatum[+M <: Var[M]] extends ListDatum[M] with MutDatum[ListDatum[M]] {

  //override type DatumType = ListType[M]

}


trait MutTaggedListDatum[M <: Var[M], N <: TypeMemberName, V <: Datum[V]] extends TaggedListDatum[M, N, V] with MutListDatum[M] {

}

//trait MuEnumDatum[D <: MuEnumDatum[D]] extends EnumDatum[D] with MuDatum[D] {
//
//}

trait MutPrimitiveDatum[+D <: PrimitiveDatum[D]] extends PrimitiveDatum[D] with MutDatum[D] {

  //override type DatumType <: PrimitiveType[D]

  //def set(native: D##::#Native) // TODO better type

}


trait MutStringDatum[+D <: StringDatum[D]] extends StringDatum[D] with MutPrimitiveDatum[D] {

  //override type DatumType = StringType[D]
  def native_=(x: StringType[D]#Native)

}


trait MutIntegerDatum[+D <: IntegerDatum[D]] extends IntegerDatum[D] with MutPrimitiveDatum[D] {

  //override type DatumType = IntegerType[D]

}


trait MutLongDatum[+D <: LongDatum[D]] extends LongDatum[D] with MutPrimitiveDatum[D] {

  //override type DatumType = LongType[D]

}


trait MutDoubleDatum[+D <: DoubleDatum[D]] extends DoubleDatum[D] with MutPrimitiveDatum[D] {

  //override type DatumType = DoubleType[D]

}


trait MutBooleanDatum[+D <: BooleanDatum[D]] extends BooleanDatum[D] with MutPrimitiveDatum[D] {

  //override type DatumType = BooleanType[D]

}
