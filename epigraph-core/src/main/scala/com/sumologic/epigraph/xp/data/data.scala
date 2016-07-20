/* Created by yegor on 5/27/16. */

package com.sumologic.epigraph.xp.data

import com.sumologic.epigraph.names.{EnumValueName, FieldName, TypeMemberName}
import com.sumologic.epigraph.xp.types._

import scala.util.{Failure, Try}

trait Var[+M <: Var[M]] {

  def getEntry[TT <: Datum[TT]](tag: Tag[_ >: M, _, TT]): Option[VarEntry[TT]]

  def varEntriesIterator: Iterator[(TypeMemberName, VarEntry[_ <: Datum[_]])]

}


// TODO rename to SelfVarType? SamoVar?
trait MonoVar[+T <: Datum[T]] extends Var[T] {
  override def getEntry[TT <: Datum[TT]](tag: Tag[_ >: T, _, TT]): Option[VarEntry[TT]] = ???
}


trait VarEntry[+T <: Datum[T]] { // TODO variance?

  def value: Try[T]

}


object VarEntry {

  val Uninitialized: Failure[Nothing] = Failure(UninitializedFieldError("Uninitialized var"))

}


trait Datum[+D <: Datum[D]] extends MonoVar[D] {

  //type DatumType <: DataType[_ <: D]

  def dataType: DataType[_ <: D]//DatumType

  def getEntry: VarEntry[D] = getEntry(dataType.default.asInstanceOf).get  // TODO asInstanceOf

  override def varEntriesIterator: Iterator[(TypeMemberName, VarEntry[_ <: Datum[_]])] =
    Iterator((dataType.default.name, getEntry))
}


trait RecordDatum[+D <: RecordDatum[D]] extends Datum[D] with Iterable[(FieldName, Var[_])] {

  //override type DatumType <: RecordType[_ <: D]
  override def dataType: RecordType[_ <: D]

  def getData[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: TaggedField[_ >: D, M, N, T]
  ): Option[T] = getData[M, N, T](field, field.tag)

  def getData[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      varTag: Tag[_ >: M, N, T]
  ): Option[T] = getValue[M, N, T](field, varTag).flatMap(_.toOption)

  // TODO getError([tagged]field[, tag])

  def getValue[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      varTag: Tag[_ >: M, N, T]
  ): Option[Try[T]] = getVarEntry[M, N, T](field, varTag).map(_.value)

  def getVarEntry[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      varTag: Tag[_ >: M, N, T]
  ): Option[VarEntry[T]] = getVar(field).flatMap(_.getEntry(varTag))

  def getVar[M <: Var[M]](field: Field[_ >: D, M]): Option[Var[M]]

  override def toString: String = {
    val sb = new StringBuilder(dataType.toString)
    sb.append(mkString("{", ",", "}"))
    sb.toString
  }

}


trait MapDatum[K <: Datum[K], +M <: Var[M]] extends Datum[MapDatum[K, M]] {

  //override type DatumType <: MapType[K, _ <: M]
  override def dataType: MapType[K, _ <: M]

  def get[N <: TypeMemberName, T <: Datum[T]](
      key: K,
      tag: Tag[_ >: M, N, T]
  ): Option[T] = ??? //getVar(key).flatMap(_.getEntry(tag))

  def getOrElse[N <: TypeMemberName, T <: Datum[T]](key: K, tag: Tag[_ >: M, N, _ <: T], default: => T): T

  def getVar(key: K): Option[M]

  //def getVarEntry[T <: Datum](key: K, varTag: VarTag[_ <: T]): Option[StaticVarEntry[_ >: M, T]]

}


trait TaggedMapDatum[K <: Datum[K], /*+*/ M <: Var[M], N <: TypeMemberName, T <: Datum[T]] extends MapDatum[K, M] {

  def tag: Tag[_ >: M, N, T]

}


trait ListDatum[+M <: Var[M]] extends Datum[ListDatum[M]] {

  //override type DatumType <: ListType[_ <: M]
  override def dataType: ListType[_ <: M]

  def head[N <: TypeMemberName, T <: Datum[T]](tag: Tag[_ >: M, N, T]): T // TODO Option[T] etc.?

}


trait TaggedListDatum[+M <: Var[M], N <: TypeMemberName, +V <: Datum[V]] extends ListDatum[M] {

  def tag: Tag[_ <: M, N, _ <: V] // FIXME `_ <: M` is not good...

  def head: V = head(tag.asInstanceOf) // FIXME this .asInstanceOf is fishy - need to provide justification why it's ok

}


trait EnumDatum[D <: EnumDatum[D]] extends Datum[D] {

  //override type DatumType <: EnumType[D]
  override def dataType: EnumType[D]

  def name: EnumValueName

}


trait PrimitiveDatum[+D <: PrimitiveDatum[D]] extends Datum[D] {

  //override type DatumType <: PrimitiveType[_ <: D]
  override def dataType: PrimitiveType[_ <: D]

  def native: Any

  override def toString: String = dataType.toString + "(" + native + ")"

}


trait StringDatum[+D <: StringDatum[D]] extends PrimitiveDatum[D] {

  //override type DatumType <: StringType[_ <: D]
  override def dataType: StringType[_ <: D]

  def native: StringType[D]#Native

  override def toString: String = dataType.toString + "(\"" + native + "\")"

}


trait IntegerDatum[+D <: IntegerDatum[D]] extends PrimitiveDatum[D] {

  //override type DatumType <: IntegerType[_ <: D]
  override def dataType: IntegerType[_ <: D]

}


trait LongDatum[+D <: LongDatum[D]] extends PrimitiveDatum[D] {

  //override type DatumType <: LongType[_ <: D]
  override def dataType: LongType[_ <: D]

}


trait DoubleDatum[+D <: DoubleDatum[D]] extends PrimitiveDatum[D] {

  //override type DatumType <: DoubleType[_ <: D]
  override def dataType: DoubleType[_ <: D]

}


trait BooleanDatum[+D <: BooleanDatum[D]] extends PrimitiveDatum[D] {

  //override type DatumType <: BooleanType[_ <: D]
  override def dataType: BooleanType[_ <: D]

}
