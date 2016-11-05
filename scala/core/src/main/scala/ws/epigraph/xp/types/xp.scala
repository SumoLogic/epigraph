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

/* Created by yegor on 5/20/16. */

package ws.epigraph.xp.types

import java.util.concurrent.ConcurrentHashMap

import ws.epigraph.names._
import ws.epigraph.xp.data._
import ws.epigraph.xp.data.builders._
import ws.epigraph.xp.data.immutable._
import ws.epigraph.xp.data.mutable._

import scala.language.higherKinds

trait Type[T] {self =>

  type This[X] <: Type[_ >: T] {type This[_] <: self.This[_ >: X]}

  def name: TypeNameApi

  def declaredSupertypes: Seq[This[_ >: T]]

  def supertypes: Seq[This[_ >: T]] = cachedSupertypes

  private lazy val cachedSupertypes: Seq[This[_ >: T]] = declaredSupertypes.flatMap { st =>
    st +: st.supertypes
  }.distinct

  /** @return `true` if `sub` is a subtype of this [[Type]] */
  def isAssignableFrom(sub: Type[_]): Boolean = sub == this || sub.supertypes.exists(_ == this) // TODO use Set?

  override def toString: String = "«" + name.string + "»" //+ "(" + super.toString + ")"

}


trait VarType[M <: Var[M]] extends Type[M] {self =>

  override type This[X] <: VarType[_ >: M] {type This[_] <: self.This[_ >: X]}

  final type DeclaredTags = Seq[Tag[M, _, _]]

  final protected def declareTags(elems: Tag[M, _, _]*): DeclaredTags = Seq[Tag[M, _, _]](elems: _*)

  def declaredVarTags: DeclaredTags

  def varTags: Seq[Tag[_ >: M, _, _]] = cachedVarTags

  // TODO correctly exclude overridden vartags?
  private lazy val cachedVarTags: Seq[Tag[_ >: M, _, _]] = (declaredVarTags ++ supertypes.flatMap(_.varTags)).distinct

  lazy val listOf: ListType[M] = new ListType[M](name.listOf, this, declaredSupertypes.map(_.listOf()), false)

  lazy val polymorphicListOf: ListType[M] = new ListType[M](
    name.listOf, this, declaredSupertypes.map(_.listOf()), true
  )

  def listOf(polymorphic: Boolean = false): ListType[M] = if (polymorphic) polymorphicListOf else listOf

  private lazy val maps = new ConcurrentHashMap[(DataType[_], Boolean), MapType[_, M]]

  def mapBy[K <: Datum[K]](keyType: DataType[K], polymorphic: Boolean = false): MapType[K, M] = {
    maps.computeIfAbsent((keyType, polymorphic), new MapTypeFunction).asInstanceOf[MapType[K, M]]
  }


  private class MapTypeFunction extends java.util.function.Function[(DataType[_], Boolean), MapType[_, M]] {

    override def apply(kp: (DataType[_], Boolean)): MapType[_, M] = mapOfType[Null](
      kp._1.asInstanceOf[DataType[Null]], kp._2
    )

    private def mapOfType[K <: Datum[K]](keyType: DataType[K], polymorphic: Boolean): MapType[K, M] = new MapType[K, M](
      name.mapBy(keyType.name),
      keyType,
      VarType.this,
      declaredSupertypes.map(_.mapBy(keyType)),
      polymorphic
    )

  }


}


class Tag[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](val name: N, val dataType: DataType[T])


class FinalTag[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
    override val name: N,
    override val dataType: DataType[T]
) extends Tag[M, N, T](name, dataType)


abstract class MultiVarType[M <: Var[M]](
    override final val name: QualifiedTypeName,
    override final val declaredSupertypes: Seq[VarType[_ >: M]] = Nil
) extends VarType[M] {

  final override type This[_] = VarType[_ >: M]

  final type AbsTag[N <: TypeMemberName, T <: Datum[T]] = Tag[M, N, T]

  def absTag[N <: TypeMemberName, T <: Datum[T]](name: N, dataType: DataType[T]): AbsTag[N, T] = new Tag[M, N, T](
    name, dataType
  )

  final type FinTag[N <: TypeMemberName, T <: Datum[T]] = FinalTag[M, N, T]

  def finTag[N <: TypeMemberName, T <: Datum[T]](name: N, dataType: DataType[T]): FinTag[N, T] = new FinalTag[M, N, T](
    name, dataType
  )

  def createMutableVar: MutVar[M] = new MutMultiVar[M]


  object MutVarConstructor extends java.util.function.Function[FieldName, MutVar[M]] {

    override def apply(t: FieldName): MutVar[M] = createMutableVar

  }


}


trait DefaultVarType[D <: Datum[D]] extends VarType[D] {this: DataType[D] =>

  val default: Tag[D, TypeMemberName.default.type, D] = new Tag[D, TypeMemberName.default.type, D](
    TypeMemberName.default, this
  )

  override lazy val declaredVarTags: DeclaredTags = declareTags(default)

}


trait DataType[D <: Datum[D]] extends Type[D] with DefaultVarType[D] {self =>

  override type This[X] <: DataType[_ >: D] {type This[_] <: self.This[_ >: X]}

  def isPolymorphic: Boolean

  def unapply(arg: Any): Option[D] = arg match {
    case datum: Datum[_] if isAssignableFrom(datum.dataType) => Some(datum.asInstanceOf[D])
    case _ => None
  }

  def createBuilder: D with DatumBuilder[D] = ??? // FIXME provide implementations in "generated" record types

}


abstract class RecordType[D <: RecordDatum[D]](
    override final val name: QualifiedTypeName,
    override final val declaredSupertypes: Seq[RecordType[_ >: D]] = Nil,
    override final val isPolymorphic: Boolean = false
) extends DataType[D] {

  final override type This[_] = RecordType[_ >: D]

  final type DeclaredFields = Seq[Field[D, _]]

  def declaredFields: DeclaredFields


  protected object DeclaredFields {

    def apply(elems: Field[D, _]*): DeclaredFields = Seq[Field[D, _]](elems: _*)

  }


  final type DatumField[T <: Datum[T]] = TaggedFinalField[D, T, TypeMemberName.default.type, T]

  protected def field[T <: Datum[T]](name: FieldName, valueType: DataType[T]): DatumField[T] =
    new TaggedFinalField[D, T, TypeMemberName.default.type, T](name, valueType, valueType.default)

  final type VarField[M <: Var[M]] = Field[D, M]

  protected def field[M <: Var[M]](name: FieldName, valueType: VarType[M]): VarField[M] =
    new AbstractField[D, M](name, valueType)

  final type VarTagField[M <: Var[M], N <: TypeMemberName, T <: Datum[T]] = TaggedField[D, M, N, T]

  protected def field[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      name: FieldName,
      valueType: VarType[M],
      tag: Tag[_ >: M, N, T]
  ): VarTagField[M, N, T] = new TaggedField[D, M, N, T](name, valueType, tag)

  def createMutable: D with MutRecordDatum[D] = ??? // FIXME provide implementations in "generated" record types

  override def createBuilder: D with RecordDatumBuilder[D] = ??? // FIXME provide implementations in "generated" record types

}


trait Field[D <: RecordDatum[D], M <: Var[M]] {

  val name: FieldName

  val valueType: VarType[M]

  def as[N <: TypeMemberName, T <: Datum[T]](varTag: Tag[_ >: M, N, T]): TaggedField[D, M, N, T] = new TaggedField[D, M, N, T](
    name, valueType, varTag
  )

}


class AbstractField[D <: RecordDatum[D], M <: Var[M]](
    override val name: FieldName,
    override val valueType: VarType[M]
) extends Field[D, M]


trait FinalField[D <: RecordDatum[D], M <: Var[M]] extends Field[D, M]


class TaggedField[D <: RecordDatum[D], M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
    override val name: FieldName,
    override val valueType: VarType[M],
    val tag: Tag[_ >: M, N, T]
) extends Field[D, M]


class FinalFieldImpl[D <: RecordDatum[D], M <: Var[M]](
    override val name: FieldName,
    override val valueType: VarType[M]
) extends FinalField[D, M]


class TaggedFinalField[D <: RecordDatum[D], M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
    name: FieldName,
    valueType: VarType[M],
    tag: Tag[_ >: M, N, T]
) extends TaggedField[D, M, N, T](name, valueType, tag) with FinalField[D, M]


class FinTaggedFinalField[D <: RecordDatum[D], M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
    name: FieldName,
    valueType: VarType[M],
    override val tag: FinalTag[_ >: M, N, T]
) extends TaggedFinalField[D, M, N, T](name, valueType, tag)


class MapType[K <: Datum[K], M <: Var[M]](
    override final val name: MapTypeNameApi,
    final val keyType: DataType[K],
    final val valueType: VarType[M],
    override final val declaredSupertypes: Seq[MapType[K, _ >: M]] = Nil,
    override final val isPolymorphic: Boolean = false
) extends DataType[MapDatum[K, M]] {

  override type This[_] = MapType[K, _ >: M]

}


class ListType[M <: Var[M]](
    override val name: ListTypeNameApi,
    val valueType: VarType[M],
    override val declaredSupertypes: Seq[ListType[_ >: M]] = Nil,
    override val isPolymorphic: Boolean = false
) extends DataType[ListDatum[M]] {

  final override type This[_] = ListType[_ >: M]

}


class TaggedListType[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
    override final val name: QualifiedTypeName,
    override final val valueType: VarType[M],
    final val tag: Tag[M, N, T],
    override final val declaredSupertypes: Seq[ListType[_ >: M]] = Nil,
    override final val isPolymorphic: Boolean = false
) extends ListType[M](name, valueType, declaredSupertypes, isPolymorphic)


trait EnumType[D <: EnumDatum[D]] extends DataType[D] {

  final override type This[_] = EnumType[D]

  def values: Seq[D]

  override def isPolymorphic: Boolean = false

  override def declaredSupertypes: Seq[EnumType[D]] = Nil

}


trait PrimitiveType[D <: PrimitiveDatum[D]] extends DataType[D] {self =>

  override type This[X] <: PrimitiveType[_ >: D] {type This[_] <: self.This[_ >: X]}

  type Native

  val NativeDefault: Native

  // TODO add default (initial) native value?

  def createImmutable(native: Native): D with ImmDatum[D]// TODO this should be defined for concrete (non-abstract) data types only

  def createMutable(native: Native): D with MutPrimitiveDatum[D]// TODO this should be defined for concrete (non-abstract) data types only

  override def createBuilder: D with PrimitiveDatumBuilder[D] = ??? // TODO this should be defined for concrete (non-abstract) data types only

  def createBuilder(native: Native): D with PrimitiveDatumBuilder[D] = ??? // TODO this should be defined for concrete (non-abstract) data types only

}


abstract class StringType[D <: StringDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[StringType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type This[_] = StringType[_ >: D]

  final override type Native = String

  final override val NativeDefault: String = ""

}


abstract class IntegerType[D <: IntegerDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[IntegerType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type This[_] = IntegerType[_ >: D]

  final override type Native = Int

  final override val NativeDefault: Int = 0

  import ws.epigraph.xp.data.immutable.ImmIntegerDatum
  import ws.epigraph.xp.data.mutable.MutIntegerDatum

  protected abstract class ImmIntegerDatumImpl(override val native: Int) extends ImmIntegerDatum[D] {this: D =>

    override def dataType: IntegerType[D] = IntegerType.this

  }


  protected abstract class MutIntegerDatumImpl(override val native: Int) extends MutIntegerDatum[D] {this: D =>

    override def dataType: IntegerType[D] = IntegerType.this

  }


  override def createBuilder: D with IntegerDatumBuilder[D] = ??? // TODO this should be defined for concrete (non-abstract) data types only

  override def createBuilder(native: Int): D with IntegerDatumBuilder[D] = ???


  // TODO this should be defined for concrete (non-abstract) data types only
  protected abstract class IntegerDatumBuilderImpl(
      override val native: Int = NativeDefault
  ) extends IntegerDatumBuilder[D] {this: D =>

    override def dataType: IntegerType[D] = IntegerType.this

  }


}


abstract class LongType[D <: LongDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[LongType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type This[_] = LongType[_ >: D]

  final override type Native = Long

  final override val NativeDefault: Long = 0L

}


abstract class DoubleType[D <: DoubleDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[DoubleType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type This[_] = DoubleType[_ >: D]

  final override type Native = Double

  final override val NativeDefault: Double = 0.0D

}


abstract class BooleanType[D <: BooleanDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[BooleanType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type This[_] = BooleanType[_ >: D]

  final override type Native = Boolean

  final override val NativeDefault: Boolean = false

}
