/* Created by yegor on 5/20/16. */

package com.sumologic.epigraph.xp.types

import java.util.concurrent.ConcurrentHashMap

import com.sumologic.epigraph.names._
import com.sumologic.epigraph.xp.data._

trait Type {

  type Super >: Null <: Type

  def name: TypeNameApi

  def declaredSupertypes: Seq[Super]

  def supertypes: Seq[Super] = cachedSupertypes

  private lazy val cachedSupertypes: Seq[Super] = declaredSupertypes.flatMap { st: Super =>
    st +: st.supertypes.asInstanceOf[Seq[Super]] // TODO explain why cast is ok
  }.distinct

  /** @return `true` if `sub` is a subtype of this [[Type]] */
  def isAssignableFrom(sub: Type): Boolean = sub == this || sub.supertypes.contains(this)

  override def toString: String = "«" + name.string + "»(" + super.toString + ")"

}


trait MultiType[M <: Var[M]] extends Type {

  override type Super >: Null <: MultiType[_ >: M]

  final type DeclaredTags = Seq[Tag[M, _, _]]

  final protected def declareTags(elems: Tag[M, _, _]*): DeclaredTags = Seq[Tag[M, _, _]](elems: _*)

  def declaredVarTags: DeclaredTags

  def varTags: Seq[Tag[_ >: M, _, _]] = cachedVarTags

  private lazy val cachedVarTags: Seq[Tag[_ >: M, _, _]] = (declaredVarTags ++ supertypes.flatMap(
    _.varTags
  )).distinct // TODO correctly exclude overridden vartags?

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
      MultiType.this,
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
    override final val declaredSupertypes: Seq[MultiType[_ >: M]] = Nil
) extends MultiType[M] {

  final override type Super = MultiType[_ >: M]

  final type AbsTag[N <: TypeMemberName, T <: Datum[T]] = Tag[M, N, T]

  def absTag[N <: TypeMemberName, T <: Datum[T]](name: N, dataType: DataType[T]): AbsTag[N, T] = new Tag[M, N, T](
    name, dataType
  )

  final type FinTag[N <: TypeMemberName, T <: Datum[T]] = FinalTag[M, N, T]

  def finTag[N <: TypeMemberName, T <: Datum[T]](name: N, dataType: DataType[T]): FinTag[N, T] = new FinalTag[M, N, T](
    name, dataType
  )

}


trait DefaultMultiType[D <: Datum[D]] extends MultiType[D] {this: DataType[D] =>

  val default: Tag[D, TypeMemberName.default.type, D] = new Tag[D, TypeMemberName.default.type, D](
    TypeMemberName.default, this
  )

  override lazy val declaredVarTags: DeclaredTags = declareTags(default)

}


trait DataType[D <: Datum[D]] extends Type with DefaultMultiType[D] {

  override type Super >: Null <: DataType[_ >: D]

  def isPolymorphic: Boolean

  def unapply(arg: Any): Option[D] = arg match {
    case datum: Datum[_] if isAssignableFrom(datum.dataType) => Some(datum.asInstanceOf[D])
    case _ => None
  }

}


abstract class RecordType[D <: RecordDatum[D]](
    override final val name: QualifiedTypeName,
    override final val declaredSupertypes: Seq[RecordType[_ >: D]] = Nil,
    override final val isPolymorphic: Boolean = false
) extends DataType[D] {

  final override type Super = RecordType[_ >: D]

  final type DeclaredFields = Seq[Field[D, _]]

  def declaredFields: DeclaredFields


  protected object DeclaredFields {

    def apply(elems: Field[D, _]*): DeclaredFields = Seq[Field[D, _]](elems: _*)

  }


  final type DatumField[T <: Datum[T]] = TaggedField[D, T, TypeMemberName.default.type, T]

  protected def field[T <: Datum[T]](name: FieldName, valueType: DataType[T]): DatumField[T] =
    new TaggedField[D, T, TypeMemberName.default.type, T](name, valueType, valueType.default)

  final type VarField[M <: Var[M]] = Field[D, M]

  protected def field[M <: Var[M]](name: FieldName, valueType: MultiType[M]): VarField[M] =
    new Field[D, M](name, valueType)

  final type VarTagField[M <: Var[M], N <: TypeMemberName, T <: Datum[T]] = TaggedField[D, M, N, T]

  protected def field[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      name: FieldName,
      valueType: MultiType[M],
      tag: Tag[_ >: M, N, T]
  ): VarTagField[M, N, T] = new TaggedField[D, M, N, T](name, valueType, tag)

}


class Field[D <: RecordDatum[D], M <: Var[M]](val name: FieldName, val valueType: MultiType[M]) {

  def as[N <: TypeMemberName, T <: Datum[T]](varTag: Tag[_ >: M, N, T]): TaggedField[D, M, N, T] = new TaggedField[D, M, N, T](
    name, valueType, varTag
  )

}


class TaggedField[D <: RecordDatum[D], M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
    override val name: FieldName,
    override val valueType: MultiType[M],
    val tag: Tag[_ >: M, N, T]
) extends Field[D, M](name, valueType)


class MapType[K <: Datum[K], M <: Var[M]](
    override final val name: MapTypeNameApi,
    final val keyType: DataType[K],
    final val valueType: MultiType[M],
    override final val declaredSupertypes: Seq[MapType[K, _ >: M]] = Nil,
    override final val isPolymorphic: Boolean = false
) extends DataType[MapDatum[K, M]] {

  override type Super = MapType[K, _ >: M]

}


class ListType[M <: Var[M]](
    override val name: ListTypeNameApi,
    val valueType: MultiType[M],
    override val declaredSupertypes: Seq[ListType[_ >: M]] = Nil,
    override val isPolymorphic: Boolean = false
) extends DataType[ListDatum[M]] {

  final override type Super = ListType[_ >: M]

}


class TaggedListType[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
    override final val name: QualifiedTypeName,
    override final val valueType: MultiType[M],
    final val tag: Tag[M, N, T],
    override final val declaredSupertypes: Seq[ListType[_ >: M]] = Nil,
    override final val isPolymorphic: Boolean = false
) extends ListType[M](name, valueType, declaredSupertypes, isPolymorphic)


trait EnumType[D <: EnumDatum[D]] extends DataType[D] {

  final override type Super = EnumType[D]

  def values: Seq[D]

  override def isPolymorphic: Boolean = false

  override def declaredSupertypes: Seq[EnumType[D]] = Nil

}


trait PrimitiveType[D <: PrimitiveDatum[D]] extends DataType[D] {

  override type Super >: Null <: PrimitiveType[_ >: D]

  type Native

  def createImmutable(native: Native): D // TODO this should be defined for concrete (non-abstract) data types only

}


abstract class StringType[D <: StringDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[StringType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = StringType[_ >: D]

  final override type Native = String

}


abstract class IntegerType[D <: IntegerDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[IntegerType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = IntegerType[_ >: D]

  final override type Native = Int

}


abstract class LongType[D <: LongDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[LongType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = LongType[_ >: D]

  final override type Native = Long

}


abstract class DoubleType[D <: DoubleDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[DoubleType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = DoubleType[_ >: D]

  final override type Native = Double

}


abstract class BooleanType[D <: BooleanDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[BooleanType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = BooleanType[_ >: D]

  final override type Native = Boolean

}
