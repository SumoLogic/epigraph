/* Created by yegor on 5/20/16. */

package com.sumologic.epigraph.xp

import java.util.concurrent.ConcurrentHashMap

import com.sumologic.epigraph.std._

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


class VarTag[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](val name: N, val dataType: DataType[T])


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


trait Type {

  type Super >: Null <: Type

  def name: TypeNameApi

  def declaredSupertypes: Seq[Super]

  def supertypes: Seq[Super] = cachedSupertypes

  private lazy val cachedSupertypes: Seq[Super] = declaredSupertypes.flatMap { st: Super =>
    st +: st.supertypes.asInstanceOf[Seq[Super]] // TODO explain why cast is ok
  }.distinct

  def isAssignableFrom(sub: Type): Boolean = sub == this || sub.supertypes.contains(this)

  override def toString: String = "«" + name.string + "»(" + super.toString + ")"

}


trait MultiType[M <: Var[M]] extends Type {

  override type Super >: Null <: MultiType[_ >: M]

  final type DeclaredTags = Seq[VarTag[M, _, _]]

  final protected def declareTags(elems: VarTag[M, _, _]*): DeclaredTags = Seq[VarTag[M, _, _]](elems: _*)

  def declaredVarTags: DeclaredTags

  def varTags: Seq[VarTag[_ >: M, _, _]] = cachedVarTags

  private lazy val cachedVarTags: Seq[VarTag[_ >: M, _, _]] = (declaredVarTags ++ supertypes.flatMap(
    _.varTags
  )).distinct // TODO correctly exclude overridden vartags?

  lazy val listOf: ListType[M] = new ListType[M](name.listOf, this, declaredSupertypes.map(_.listOf()), false)

  lazy val polymorphicListOf: ListType[M] = new ListType[M](
    name.listOf, this, declaredSupertypes.map(_.listOf()), true
  )

  def listOf(polymorphic: Boolean = false): ListType[M] = if (polymorphic) polymorphicListOf else listOf

  private lazy val maps = new ConcurrentHashMap[(DataType[_], Boolean), MapType[_, M]]

  def mapBy[K <: Datum[K]](keyType: DataType[K], polymorphic: Boolean = false): MapType[K, M] = {
    maps.computeIfAbsent((keyType, polymorphic), MapTypeFunction).asInstanceOf[MapType[K, M]]
  }


  object MapTypeFunction extends java.util.function.Function[(DataType[_], Boolean), MapType[_, M]] {

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


abstract class MultiVarType[M <: Var[M]](
    override final val name: QualifiedTypeName,
    override final val declaredSupertypes: Seq[MultiType[_ >: M]] = Nil
) extends MultiType[M] {

  final override type Super = MultiType[_ >: M]

  final type Tag[N <: TypeMemberName, T <: Datum[T]] = VarTag[M, N, T]

  def tag[N <: TypeMemberName, T <: Datum[T]](name: N, dataType: DataType[T]): Tag[N, T] = new VarTag[M, N, T](
    name, dataType
  )

}


trait DefaultMultiType[D <: Datum[D]] extends MultiType[D] {this: DataType[D] =>

  val default: VarTag[D, TypeMemberName.default.type, D] = new VarTag[D, TypeMemberName.default.type, D](
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
      tag: VarTag[_ >: M, N, T]
  ): VarTagField[M, N, T] = new TaggedField[D, M, N, T](name, valueType, tag)

}


class Field[D <: RecordDatum[D], M <: Var[M]](val name: FieldName, val valueType: MultiType[M]) {

  def as[N <: TypeMemberName, T <: Datum[T]](varTag: VarTag[_ >: M, N, T]): TaggedField[D, M, N, T] = new TaggedField[D, M, N, T](
    name, valueType, varTag
  )

}


class TaggedField[D <: RecordDatum[D], M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
    override val name: FieldName,
    override val valueType: MultiType[M],
    val tag: VarTag[_ >: M, N, T]
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
    final val tag: VarTag[M, N, T],
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

}


abstract class StringType[D <: StringDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[StringType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = StringType[_ >: D]

  final override type Native = String

}


class IntegerType[D <: IntegerDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[IntegerType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = IntegerType[_ >: D]

  final override type Native = Int

}


class LongType[D <: LongDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[LongType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = LongType[_ >: D]

  final override type Native = Long

}


class DoubleType[D <: DoubleDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[DoubleType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = DoubleType[_ >: D]

  final override type Native = Double

}


class BooleanType[D <: BooleanDatum[D]](
    override val name: QualifiedTypeName,
    override val declaredSupertypes: Seq[BooleanType[_ >: D]] = Nil,
    override val isPolymorphic: Boolean = false
) extends PrimitiveType[D] {

  final override type Super = BooleanType[_ >: D]

  final override type Native = Boolean

}
