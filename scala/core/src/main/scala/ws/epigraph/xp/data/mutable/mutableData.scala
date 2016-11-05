/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 5/27/16. */

package ws.epigraph.xp.data.mutable

import java.util.Map.Entry
import java.util.concurrent.ConcurrentHashMap

import ws.epigraph.names.{FieldName, TypeMemberName}
import ws.epigraph.xp.data._
import ws.epigraph.xp.data.immutable.ImmVarEntry
import ws.epigraph.xp.types._

import scala.collection.JavaConversions
import scala.language.existentials
import scala.util.{Failure, Success, Try}

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

  private val entries = new ConcurrentHashMap[TypeMemberName, VarEntry[_ <: Datum[_]]]

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


  override def varEntriesIterator: Iterator[(TypeMemberName, VarEntry[_ <: Datum[_]])] =
    JavaConversions.asScalaIterator(entries.entrySet().iterator()).map { e =>
      Tuple2[TypeMemberName, VarEntry[_ <: Datum[_]]](e.getKey, e.getValue) // TODO WTF case class constructor doesn't work
    }
}


class MutVarEntry[T <: Datum[T]](
    /*override val tag: VarTag[_, _, T],*/
    var value: Try[T] = VarEntry.Uninitialized
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


trait MutDatum[+D <: Datum[D]] extends Datum[D] {this: D =>

  val entry: VarEntry[D] = new ImmVarEntry[D](Success(this)) // TODO make sure it's ok to keep it immutable

  private lazy val someEntry: Some[VarEntry[D]] = Some(entry)

  override def getEntry[T <: Datum[T]](tag: Tag[_ >: D, _, T]): Option[VarEntry[T]] = {
    // FIXME check tag is ours or compatible with (i.e. supertype's)
    someEntry.asInstanceOf[Option[VarEntry[T]]] // TODO check this cast!
  }

  override def varEntriesIterator: Iterator[(TypeMemberName, VarEntry[_ <: Datum[_]])] =
    Iterator((TypeMemberName.default, entry))

}


trait MutRecordDatum[+D <: RecordDatum[D]] extends RecordDatum[D] with MutDatum[D] {this: D =>

  //override type DatumType = RecordType[D]

  private val vars = new ConcurrentHashMap[FieldName, Var[M] forSome { type M <: Var[M]}]

  override def iterator: Iterator[(FieldName, Var[_])] = {
    JavaConversions.asScalaIterator(vars.entrySet().iterator()).map(e => (e.getKey, e.getValue))
  }

  override def getVar[M <: Var[M]](field: Field[_ >: D, M]): Option[Var[M]] = {
    Option(vars.get(field.name).asInstanceOf[Var[M]]) // TODO explain why ok
  }

  def setData[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: FinTaggedFinalField[_ >: D, M, N, T],
      data: => T
  ): this.type = setData[M, N, T](field, field.tag, data)

  def setData[N <: TypeMemberName, T <: Datum[T]](
      field: TaggedFinalField[_ >: D, T, N, T],
      data: => T
  ): this.type = {
    vars.put(field.name, data)
    this
  }

  def setData[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: FinalField[_ >: D, M],
      tag: FinalTag[_ >: M, N, T],
      data: => T
  ): this.type = setValue[M, N, T](field, tag, Try(data))

  // TODO getError([tagged]field[, tag])

  def setValue[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: FinalField[_ >: D, M],
      tag: FinalTag[_ >: M, N, T],
      value: Try[T]
  ): this.type = {
    getOrCreateVarEntry[M, N, T](field, tag).value = value
    this
  }

  def getOrCreateVarEntry[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: FinalField[_ >: D, M],
      tag: FinalTag[_ >: M, N, T]
  ): MutVarEntry[T] = getOrCreateVar(field).getOrCreateEntry(tag)

  def getOrCreateVar[M <: Var[M]](field: FinalField[_ >: D, M]): MutVar[M] = {
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

trait MutPrimitiveDatum[+D <: PrimitiveDatum[D]] extends MutDatum[D] with PrimitiveDatum[D] {this: D =>}


abstract class MutPrimitiveDatumImpl[+D <: PrimitiveDatum[D]](
    override val dataType: PrimitiveType[_ <: D]
) extends MutDatum[D] with PrimitiveDatum[D] {this: D =>}


trait MutStringDatum[+D <: StringDatum[D]] extends MutPrimitiveDatum[D] with StringDatum[D] {this: D =>

  def native_=(native: StringType[D]#Native): Unit

}


abstract class MutStringDatumImpl[+D <: StringDatum[D]](
    override val dataType: StringType[_ <: D],
    var native: String
) extends MutPrimitiveDatumImpl[D](dataType) with MutStringDatum[D] {this: D =>}


trait MutIntegerDatum[+D <: IntegerDatum[D]] extends MutPrimitiveDatum[D] with IntegerDatum[D] {this: D =>}


abstract class MutIntegerDatumImpl[+D <: IntegerDatum[D]](
    override val dataType: IntegerType[_ <: D],
    var native: Integer
) extends MutPrimitiveDatumImpl[D](dataType) with MutIntegerDatum[D] {this: D =>}


trait MutLongDatum[+D <: LongDatum[D]] extends MutPrimitiveDatum[D] with LongDatum[D] {this: D =>}


abstract class MutLongDatumImpl[+D <: LongDatum[D]](
    override val dataType: LongType[_ <: D],
    var native: Long
) extends MutPrimitiveDatumImpl[D](dataType) with MutLongDatum[D] {this: D =>}


trait MutDoubleDatum[+D <: DoubleDatum[D]] extends MutPrimitiveDatum[D] with DoubleDatum[D] {this: D =>}


abstract class MutDoubleDatumImpl[+D <: DoubleDatum[D]](
    override val dataType: DoubleType[_ <: D],
    var native: Double
) extends MutPrimitiveDatumImpl[D](dataType) with MutDoubleDatum[D] {this: D =>}


trait MutBooleanDatum[+D <: BooleanDatum[D]] extends MutPrimitiveDatum[D] with BooleanDatum[D] {this: D =>}


abstract class MutBooleanDatumImpl[+D <: BooleanDatum[D]](
    override val dataType: BooleanType[_ <: D],
    var native: Boolean
) extends MutPrimitiveDatumImpl[D](dataType) with MutBooleanDatum[D] {this: D =>}
