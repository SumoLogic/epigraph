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

/* Created by yegor on 6/8/16. */

package ws.epigraph.xp.data.builders

import java.util.concurrent.ConcurrentHashMap

import ws.epigraph.names.{FieldName, TypeMemberName}
import ws.epigraph.xp.data._
import ws.epigraph.xp.data.immutable.ImmVarEntry
import ws.epigraph.xp.types._

import scala.collection.JavaConversions
import scala.language.existentials
import scala.util.{Failure, Success, Try}

trait VarBuilder[M <: Var[M]] extends Var[M] { // TODO take/require vartype?

  override def getEntry[TT <: Datum[TT]](tag: Tag[_ >: M, _, TT]): Option[VarEntryBuilder[TT]]

  def getOrCreateEntry[TT <: Datum[TT]](tag: Tag[_ >: M, _ <: TypeMemberName, TT]): VarEntryBuilder[TT]

  final def setData[TT <: Datum[TT]](tag: Tag[_ >: M, _, TT], data: => TT): this.type = setValue(tag, Try(data))

  final def setError[TT <: Datum[TT]](tag: Tag[_ >: M, _, TT], error: => Throwable): this.type = setValue(
    tag, Failure(error)
  )

  def setValue[TT <: Datum[TT]](tag: Tag[_ >: M, _, TT], value: => Try[TT]): this.type

}

class MultiVarBuilder[M <: Var[M]] extends VarBuilder[M] {

  private val entries = new ConcurrentHashMap[TypeMemberName, VarEntryBuilder[_]]

  override def getEntry[TT <: Datum[TT]](tag: Tag[_ >: M, _, TT]): Option[VarEntryBuilder[TT]] = Option[VarEntryBuilder[TT]](
    entries.get(tag.name).asInstanceOf[VarEntryBuilder[TT]] // TODO explain cast
  )

  override def getOrCreateEntry[TT <: Datum[TT]](tag: Tag[_ >: M, _ <: TypeMemberName, TT]): VarEntryBuilder[TT] = {
    // TODO find most narrow tag (two different inherited once must've been merged into local or failed in compile)
    entries.computeIfAbsent(tag.name, new MuVarEntryConstructor/*(tag)*/).asInstanceOf[VarEntryBuilder[TT]]
  }


  // TODO: take this from the tag itself (and lazy there)?
  private class MuVarEntryConstructor[TT <: Datum[TT]]/*(private val tag: Tag[_, _, TT])*/
      extends java.util.function.Function[TypeMemberName, VarEntryBuilder[TT]] {

    override def apply(t: TypeMemberName): VarEntryBuilder[TT] = new VarEntryBuilder[TT]/*(tag)*/

  }


  @throws[RuntimeException]("If super tag data type is wider than overridden tag")
  override def setValue[TT <: Datum[TT]](
      tag: Tag[_ >: M, _, TT],
      value: => Try[TT]
  ): this.type = ???

  override def varEntriesIterator: Iterator[(TypeMemberName, VarEntry[_ <: Datum[_]])] =
    JavaConversions.asScalaIterator(entries.entrySet().iterator()).map { e =>
      Tuple2[TypeMemberName, VarEntry[_ <: Datum[_]]](e.getKey, e.getValue.asInstanceOf) // TODO WTF case class constructor doesn't work? Why do we still need asInstanceOf?
    }

}


class VarEntryBuilder[T <: Datum[T]](
    /*override val tag: VarTag[_, _, T],*/
    var value: Try[T] = VarEntry.Uninitialized
) extends VarEntry[T] {

  def this(/*tag: Tag[_, _, T], */ data: => T) = this(/*tag, */ Try(data))

  def this(/*tag: Tag[_, _, T], */ error: Throwable) = this(/*tag, */ Failure(error))

  def setData(data: => T): this.type = {
    value = Try(data)
    this
  }

  def setError(error: Throwable): this.type = {
    value = Failure(error)
    this
  }

}


object VarEntryBuilder {

  val Uninitialized: Failure[Nothing] = Failure(UninitializedFieldError("Uninitialized var"))

}


trait DatumBuilder[D <: Datum[D]] extends Datum[D] {this: D =>

  val entry: VarEntryBuilder[D] = new VarEntryBuilder[D](Success(this)) // TODO untangle

  private lazy val someEntry: Some[VarEntryBuilder[D]] = Some(entry)

  override def getEntry[T <: Datum[T]](tag: Tag[_ >: D, _, T]): Option[VarEntryBuilder[T]] = {
    // FIXME check tag is ours or compatible with (i.e. supertype's)
    someEntry.asInstanceOf[Option[VarEntryBuilder[T]]] // TODO check this cast!
  }

}


trait RecordDatumBuilder[D <: RecordDatum[D]] extends RecordDatum[D] with DatumBuilder[D] {this: D =>

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
      field: Field[_ >: D, M],
      tag: Tag[_ >: M, N, T],
      data: => T
  ): this.type = setValue[M, N, T](field, tag, Try(data))

  // TODO getError([tagged]field[, tag])

  def setValue[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      tag: Tag[_ >: M, N, T],
      value: Try[T]
  ): this.type = {
    getOrCreateVarEntry[M, N, T](field, tag).value = value
    this
  }

  def getOrCreateVarEntry[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      tag: Tag[_ >: M, N, T]
  ): VarEntryBuilder[T] = getOrCreateVar(field).getOrCreateEntry(tag)

  def getOrCreateVar[M <: Var[M]](field: Field[_ >: D, M]): VarBuilder[M] = {
    vars.computeIfAbsent(field.name, new MuVarConstructor/*(field)*/).asInstanceOf[VarBuilder[M]]
  }


  // TODO: take this from the field itself (and lazy there)? convert to an object?
  private class MuVarConstructor[M <: Var[M]]/*(private val field: Field[_ >: D, M])*/
      extends java.util.function.Function[FieldName, VarBuilder[M]] {

    override def apply(t: FieldName): VarBuilder[M] = new MultiVarBuilder[M]

  }


}


trait MapDatumBuilder[K <: Datum[K], M <: Var[M]] extends MapDatum[K, M] with DatumBuilder[MapDatum[K, M]] {

  //override type DatumType = MapType[K, M]

  def getVar(key: K): Option[M]

  //def getVarEntry[T <: Datum](key: K, varTag: VarTag[_ <: T]): Option[StaticVarEntry[_ >: M, T]]

}


trait TaggedMapDatumBuilder[K <: Datum[K], /*+*/ M <: Var[M], N <: TypeMemberName, T <: Datum[T]]
    extends TaggedMapDatum[K, M, N, T] with MapDatumBuilder[K, M]


trait ListDatumBuilder[M <: Var[M]] extends ListDatum[M] with DatumBuilder[ListDatum[M]] {

  //override type DatumType = ListType[M]

}


trait TaggedListDatumBuilder[M <: Var[M], N <: TypeMemberName, V <: Datum[V]] extends TaggedListDatum[M, N, V] with ListDatumBuilder[M] {

}

//trait MuEnumDatum[D <: MuEnumDatum[D]] extends EnumDatum[D] with MuDatum[D] {
//
//}

trait PrimitiveDatumBuilder[D <: PrimitiveDatum[D]] extends DatumBuilder[D] with PrimitiveDatum[D] {this: D =>}


abstract class PrimitiveDatumBuilderImpl[D <: PrimitiveDatum[D]](
    override val dataType: PrimitiveType[_ <: D]
) extends DatumBuilder[D] with PrimitiveDatum[D] {this: D =>}


trait StringDatumBuilder[D <: StringDatum[D]] extends PrimitiveDatumBuilder[D] with StringDatum[D] {this: D =>

  def native_=(native: StringType[D]#Native): Unit

}


abstract class StringDatumBuilderImpl[D <: StringDatum[D]](
    override val dataType: StringType[_ <: D],
    var native: String
) extends PrimitiveDatumBuilderImpl[D](dataType) with StringDatumBuilder[D] {this: D =>}


trait IntegerDatumBuilder[D <: IntegerDatum[D]] extends PrimitiveDatumBuilder[D] with IntegerDatum[D] {this: D =>}


abstract class IntegerDatumBuilderImpl[D <: IntegerDatum[D]](
    override val dataType: IntegerType[_ <: D],
    var native: Integer
) extends PrimitiveDatumBuilderImpl[D](dataType) with IntegerDatumBuilder[D] {this: D =>}


trait LongDatumBuilder[D <: LongDatum[D]] extends PrimitiveDatumBuilder[D] with LongDatum[D] {this: D =>}


abstract class LongDatumBuilderImpl[D <: LongDatum[D]](
    override val dataType: LongType[_ <: D],
    var native: Long
) extends PrimitiveDatumBuilderImpl[D](dataType) with LongDatumBuilder[D] {this: D =>}


trait DoubleDatumBuilder[D <: DoubleDatum[D]] extends PrimitiveDatumBuilder[D] with DoubleDatum[D] {this: D =>}


abstract class DoubleDatumBuilderImpl[D <: DoubleDatum[D]](
    override val dataType: DoubleType[_ <: D],
    var native: Double
) extends PrimitiveDatumBuilderImpl[D](dataType) with DoubleDatumBuilder[D] {this: D =>}


trait BooleanDatumBuilder[D <: BooleanDatum[D]] extends PrimitiveDatumBuilder[D] with BooleanDatum[D] {this: D =>}


abstract class BooleanDatumBuilderImpl[D <: BooleanDatum[D]](
    override val dataType: BooleanType[_ <: D],
    var native: Boolean
) extends PrimitiveDatumBuilderImpl[D](dataType) with BooleanDatumBuilder[D] {this: D =>}
