/* Created by yegor on 5/12/16. */

package com.sumologic.epigraph.std

trait Type {

  type Super <: Type

  def name: QualifiedTypeName

  def declaredSupertypes: Seq[Super]

  def supertypes: Seq[Super] = cachedSupertypes

  private lazy val cachedSupertypes: Seq[Super] = declaredSupertypes.flatMap(_.supertypes.asInstanceOf).distinct

  def isAssignableFrom(sub: Type): Boolean = sub == this || sub.supertypes.contains(this)

}


trait MultiType[M <: MultiVar[M]] extends Type {

  override type Super <: MultiType[_ >: M]

  def declaredVarTags: Seq[VarTag[M, _]]

  def varTags: Seq[VarTag[_ >: M, _]] = cachedVarTags

  private lazy val cachedVarTags: Seq[VarTag[_ >: M, _]] = (declaredVarTags ++ supertypes.flatMap(_.varTags)).distinct

  def /*lazy val*/ listOf: ListType[Null, M] // = new ListType[Null, M](name.listOf, supertypes.map(_.listOf), this)

}


trait DefaultMultiType[D <: Datum[D]] extends MultiType[D] {this: DataType[D] =>

  val default: VarTag[D, D] = new VarTag[D, D] { // TODO class

    override def name: TypeMemberName = TypeMemberName.default

    override def declaredDataType: DataType[D] = DefaultMultiType.this

  }

  override def declaredVarTags: Seq[VarTag[D, D]] = Seq[VarTag[D, D]](default)

  override def listOf: ListType[Null, D] = ???

}


trait DataType[D <: Datum[D]] extends Type with DefaultMultiType[D] {

  override type Super <: DataType[_ >: D]

  def isPolymorphic: Boolean

  def unapply(arg: Any): Option[D] = arg match {
    case datum: Datum[_] if isAssignableFrom(datum.dataType) => Some(datum.asInstanceOf[D])
    case _ => None
  }

}


trait RecordType[D <: RecordDatum[D]] extends DataType[D] {

  final override type Super = RecordType[_ >: D]

  def declaredFields: Seq[Field[D, _]]

}


trait MapType[D <: MapDatum[D, K, M], K <: Datum[K], M <: MultiVar[M]] extends DataType[D] {

  override type Super = MapType[_ >: D, K, _ >: M]

  def keyType: DataType[K]

  def valueType: MultiType[M]

}


trait ListType[D <: ListDatum[D, M], M <: MultiVar[M]] extends DataType[D] {

  override type Super = ListType[_ >: D, _ >: M]

  def valueType: MultiType[M]

}


trait EnumType[D <: EnumDatum[D]] extends DataType[D] {

  override type Super = EnumType[D]

  def values: Seq[D]

  override def isPolymorphic: Boolean = false

  override def declaredSupertypes: Seq[EnumType[D]] = Nil

}


trait PrimitiveType[D <: PrimitiveDatum[D]] extends DataType[D] {

  override type Super <: PrimitiveType[_ >: D]

  type Native

}


trait StringType[D <: StringDatum[D]] extends PrimitiveType[D] {

  override type Super = StringType[_ >: D]

  final override type Native = String

}


trait IntegerType[D <: IntegerDatum[D]] extends PrimitiveType[D] {

  override type Super = IntegerType[_ >: D]

  final override type Native = Int

}


trait LongType[D <: LongDatum[D]] extends PrimitiveType[D] {

  override type Super = LongType[_ >: D]

  final override type Native = Long

}


trait DoubleType[D <: DoubleDatum[D]] extends PrimitiveType[D] {

  override type Super = DoubleType[_ >: D]

  final override type Native = Double

}


trait BooleanType[D <: BooleanDatum[D]] extends PrimitiveType[D] {

  override type Super = BooleanType[_ >: D]

  final override type Native = Boolean

}
