/* Created by yegor on 5/23/16. */

package epigraph.schema

import com.sumologic.epigraph.names
import com.sumologic.epigraph.xp._
import com.sumologic.epigraph.xp.data.{ListDatum, RecordDatum, Var}
import com.sumologic.epigraph.xp.types.{MultiVarType, RecordType}
import epigraph._

/**
 * {{{
 * vartype ByNameRef default name {
 *   doc = "Common interface for vartypes representing by-name references";
 *   name: Name
 * }
 * }}}
 */
trait ByNameRef extends Var[ByNameRef]


object ByNameRef extends MultiVarType[ByNameRef](ns \ "ByNameRef") {

  val `"name"`: names.TypeMemberName = "name"

  val _name: Tag[`"name"`.type, Name] = tag(`"name"`, Name)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_name)

}


/**
 * {{{
 * abstract record TypeData extends Named {
 *   doc = "Common interface for data type and vartype records";
 *   override name: QualifiedTypeName
 *   abstract supertypes: list[TypeRef]
 * }
 * }}}
 */
trait TypeData extends Named with RecordDatum[TypeData]


object TypeData extends RecordType[TypeData](ns \ "TypeData", Seq(Named)) {

  val _name: DatumField[QualifiedTypeName] = field("name", QualifiedTypeName)

  val _supertypes: DatumField[ListDatum[TypeRef]] = field("supertypes", TypeRef.listOf)

  override def declaredFields: DeclaredFields = DeclaredFields(_name, _supertypes)

}


/**
 * {{{
 * vartype TypeRef extends ByNameRef {
 *   doc = "Common interface for by-name references to (data or var-) types";
 *   override name: QualifiedTypeName
 *   type: TypeData
 * }
 * }}}
 */
trait TypeRef extends ByNameRef with Var[TypeRef]


object TypeRef extends MultiVarType[TypeRef](ns \ "TypeRef", Seq(ByNameRef)) {

  val _name: Tag[ByNameRef.`"name"`.type, QualifiedTypeName] = tag(ByNameRef.`"name"`, QualifiedTypeName)

  val `"type"`: names.TypeMemberName = "type"

  val _type: Tag[`"type"`.type, TypeData] = tag(`"type"`, TypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_name, _type)

}


/**
 * VarType declaration
 *
 * {{{
 * record VarTypeData extends TypeData {
 *   doc = "Vartype declaration";
 *   override supertypes: list[VarTypeRef]
 *   `default`: TypeMemberRef {
 *     doc = "Optional type member reference to be used as default one";
 *   }
 *   members: list[TypeMemberData] // TODO rename to `tags`?
 * }
 * }}}
 */
trait VarTypeData extends TypeData with RecordDatum[VarTypeData]


object VarTypeData extends RecordType[VarTypeData](ns \ "TypeData", Seq(TypeData)) {

  val _supertypes: DatumField[ListDatum[VarTypeRef]] = field("supertypes", VarTypeRef.listOf)

  val _default: VarTagField[TypeMemberRef, ByNameRef.`"name"`.type, TypeMemberName] = field(
    "default", TypeMemberRef, TypeMemberRef._name
  )

  val _members: DatumField[ListDatum[TypeMemberData]] = field("members", TypeMemberData.listOf)

  override def declaredFields: DeclaredFields = DeclaredFields(_supertypes, _default, _members)

}


/**
 * {{{
 * vartype VarTypeRef extends TypeRef {
 *   doc = "By-name reference to a vartype";
 *   override type: VarTypeData
 * }
 * }}}
 */
trait VarTypeRef extends TypeRef with Var[VarTypeRef]


object VarTypeRef extends MultiVarType[VarTypeRef](ns \ "VarTypeRef", Seq(TypeRef)) {

  val _name: TypeRef.Tag[ByNameRef.`"name"`.type, QualifiedTypeName] = TypeRef._name

  val _type: Tag[TypeRef.`"type"`.type, VarTypeData] = tag(TypeRef.`"type"`, VarTypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_type)

}


/**
 * {{{
 * record TypeMemberData extends Named {
 *   doc = "Vartype member declaration";
 *   override name: TypeMemberName
 *   dataType: DataTypeRef
 * }
 * }}}
 */
trait TypeMemberData extends Named with RecordDatum[TypeMemberData]


object TypeMemberData extends RecordType[TypeMemberData](ns \ "TypeMemberData", Seq(Named)) {

  val _name: DatumField[TypeMemberName] = field("name", TypeMemberName)

  val _dataType: VarTagField[DataTypeRef, ByNameRef.`"name"`.type, QualifiedTypeName] =
    field[DataTypeRef, ByNameRef.`"name"`.type, QualifiedTypeName]("dataType", DataTypeRef, DataTypeRef._name)

  override def declaredFields: DeclaredFields = DeclaredFields(_name, _dataType)

}


/**
 * {{{
 * vartype TypeMemberRef extends ByNameRef {
 *   doc = "By-name reference to vartype member";
 *   override name: TypeMemberName
 *   member: TypeMember
 * }
 * }}}
 */
trait TypeMemberRef extends ByNameRef with Var[TypeMemberRef]


object TypeMemberRef extends MultiVarType[TypeMemberRef](ns \ "TypeMemberRef", Seq(ByNameRef)) {

  import ByNameRef.`"name"`

  val _name: Tag[`"name"`.type, TypeMemberName] = tag(`"name"`, TypeMemberName)

  val `"member"`: names.TypeMemberName = "member"

  val _member: Tag[`"member"`.type, TypeMemberData] = tag(`"member"`, TypeMemberData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_name, _member)

}


/**
 * {{{
 * polymorphic record DataTypeData extends TypeData {
 *   override supertypes: list[DataTypeRef]
 *   polymorphic: Boolean
 *   metaType: DataTypeRef
 * }
 * }}}
 */
trait DataTypeData extends TypeData with RecordDatum[DataTypeData]


object DataTypeData extends RecordType[DataTypeData](ns \ "DataTypeData", Seq(TypeData), true) {

  val _supertypes: DatumField[ListDatum[DataTypeRef]] = field("supertypes", DataTypeRef.listOf)

  val _polymorphic: DatumField[BooleanPrimitive] = field("polymorphic", BooleanPrimitive)

  val _metaType: VarTagField[DataTypeRef, ByNameRef.`"name"`.type, QualifiedTypeName] =
    field[DataTypeRef, ByNameRef.`"name"`.type, QualifiedTypeName]("metaType", DataTypeRef, DataTypeRef._name)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_supertypes, _polymorphic, _metaType)

}


/**
 * {{{
 * vartype DataTypeRef extends TypeRef {
 *   doc = "By-name reference to data type";
 *   override type: DataTypeData
 * }
 * }}}
 */
trait DataTypeRef extends TypeRef with Var[DataTypeRef]


object DataTypeRef extends MultiVarType[DataTypeRef](ns \ "DataTypeRef", Seq(TypeRef)) {

  import TypeRef.`"type"`

  val _name: TypeRef.Tag[ByNameRef.`"name"`.type, QualifiedTypeName] = TypeRef._name

  val _type: Tag[`"type"`.type, DataTypeData] = tag(`"type"`, DataTypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_type)

}


/**
 * {{{
 * record RecordTypeData extends DataTypeData {
 *   override supertypes: list[RecordTypeRef]
 *   declaredFields: list[FieldData]
 * }
 * }}}
 */
trait RecordTypeData extends DataTypeData with RecordDatum[RecordTypeData]


object RecordTypeData extends RecordType[RecordTypeData](ns \ "RecordTypeData", Seq(DataTypeData)) {

  val _supertypes: DatumField[ListDatum[RecordTypeRef]] = field("supertypes", RecordTypeRef.listOf)

  val _declaredFields: DatumField[ListDatum[FieldData]] = field("declaredFields", FieldData.listOf)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_supertypes, _declaredFields)

}


/**
 * {{{
 * vartype RecordTypeRef extends DataTypeRef {
 *   override type: RecordTypeData
 * }
 * }}}
 */
trait RecordTypeRef extends DataTypeRef with Var[RecordTypeRef]


object RecordTypeRef extends MultiVarType[RecordTypeRef](ns \ "RecordTypeRef", Seq(DataTypeRef)) {

  val _type: Tag[TypeRef.`"type"`.type, RecordTypeData] = tag(TypeRef.`"type"`, RecordTypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_type)

}


/**
 * source: {{{
 * record FieldData extends Named {
 *   override name: FieldName
 *   valueType: VarTypeRef
 *   `default`: TypeMemberRef
 * }
 * }}}
 */
trait FieldData extends Named with RecordDatum[FieldData]


object FieldData extends RecordType[FieldData](ns \ "FieldData", Seq(Named)) {

  val _name: DatumField[FieldName] = field("name", FieldName)

  val _valueType: VarTagField[VarTypeRef, ByNameRef.`"name"`.type, QualifiedTypeName] =
    field[VarTypeRef, ByNameRef.`"name"`.type, QualifiedTypeName]("valueType", VarTypeRef, VarTypeRef._name)

  val _default: VarTagField[TypeMemberRef, ByNameRef.`"name"`.type, TypeMemberName] =
    field("default", TypeMemberRef, TypeMemberRef._name)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_name, _valueType, _default)

}


/**
 * {{{
 * record MapTypeData extends DataTypeData {
 *   override supertypes: list[MapTypeRef]
 *   keyType: DataTypeRef
 *   valueType: VarTypeRef
 * }
 * }}}
 */
trait MapTypeData extends DataTypeData with RecordDatum[MapTypeData]


object MapTypeData extends RecordType[MapTypeData](ns \ "MapTypeData", Seq(DataTypeData)) {

  val _supertypes: DatumField[ListDatum[MapTypeRef]] = field("supertypes", MapTypeRef.listOf)

  val _keyType: VarTagField[DataTypeRef, ByNameRef.`"name"`.type, QualifiedTypeName] =
    field[DataTypeRef, ByNameRef.`"name"`.type, QualifiedTypeName]("keyType", DataTypeRef, DataTypeRef._name)

  val _valueType: VarTagField[VarTypeRef, ByNameRef.`"name"`.type, QualifiedTypeName] =
    field[VarTypeRef, ByNameRef.`"name"`.type, QualifiedTypeName]("valueType", VarTypeRef, VarTypeRef._name)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_supertypes, _keyType, _valueType)

}


/**
 * {{{
 * vartype MapTypeRef extends DataTypeRef { type: MapTypeData }
 * }}}
 */
trait MapTypeRef extends DataTypeRef with Var[MapTypeRef]


object MapTypeRef extends MultiVarType[MapTypeRef](ns \ "MapTypeRef", Seq(DataTypeRef)) {

  val _type: Tag[TypeRef.`"type"`.type, MapTypeData] = tag(TypeRef.`"type"`, MapTypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_type)

}


/**
 * {{{
 * record ListTypeData extends DataTypeData {
 *   override supertypes: list[ListTypeRef]
 *   valueType: VarTypeRef
 * }
 * }}}
 */
trait ListTypeData extends DataTypeData with RecordDatum[ListTypeData]


object ListTypeData extends RecordType[ListTypeData](ns \ "ListTypeData", Seq(DataTypeData)) {

  val _supertypes: DatumField[ListDatum[ListTypeRef]] = field("supertypes", ListTypeRef.listOf)

  val _valueType: VarTagField[VarTypeRef, ByNameRef.`"name"`.type, QualifiedTypeName] =
    field[VarTypeRef, ByNameRef.`"name"`.type, QualifiedTypeName]("valueType", VarTypeRef, VarTypeRef._name)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_supertypes, _valueType)

}


/**
 * {{{
 * vartype ListTypeRef extends DataTypeRef { type: ListTypeData }
 * }}}
 */
trait ListTypeRef extends DataTypeRef with Var[ListTypeRef]


object ListTypeRef extends MultiVarType[ListTypeRef](ns \ "ListTypeRef", Seq(DataTypeRef)) {

  val _type: Tag[TypeRef.`"type"`.type, ListTypeData] = tag(TypeRef.`"type"`, ListTypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_type)

}


/**
 * {{{
 * record EnumTypeData extends DataTypeData {
 *   override supertypes: list[EnumTypeRef]
 * //valueType: DataTypeRef?
 *   values: list[EnumValueData]
 * }
 * }}}
 */
trait EnumTypeData extends DataTypeData with RecordDatum[EnumTypeData]


object EnumTypeData extends RecordType[EnumTypeData](ns \ "EnumTypeData", Seq(DataTypeData)) {

  val _supertypes: DatumField[ListDatum[EnumTypeRef]] = field("supertypes", EnumTypeRef.listOf)

  val _values: DatumField[ListDatum[EnumValueData]] = field("values", EnumValueData.listOf)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_supertypes, _values)

}


/**
 * {{{
 * vartype EnumTypeRef extends DataTypeRef { override type: EnumTypeData }
 * }}}
 */
trait EnumTypeRef extends DataTypeRef with Var[EnumTypeRef]


object EnumTypeRef extends MultiVarType[EnumTypeRef](ns \ "EnumTypeRef", Seq(DataTypeRef)) {

  val _type: Tag[TypeRef.`"type"`.type, EnumTypeData] = tag(TypeRef.`"type"`, EnumTypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_type)

}


/**
 * {{{
 * record EnumValueData extends Named { override name: EnumValueName } // TODO value??
 * }}}
 */
trait EnumValueData extends Named with RecordDatum[EnumValueData]


object EnumValueData extends RecordType[EnumValueData](ns \ "EnumValueData", Seq(Named)) {

  val _name: DatumField[EnumValueName] = field("name", EnumValueName)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_name)

}


/**
 * {{{
 * abstract polymorphic record PrimitiveTypeData extends DataTypeData { override supertypes: list[PrimitiveTypeRef] }
 * }}}
 */
trait PrimitiveTypeData extends DataTypeData with RecordDatum[PrimitiveTypeData]


object PrimitiveTypeData extends RecordType[PrimitiveTypeData](ns \ "PrimitiveTypeData", Seq(DataTypeData), true) {

  val _supertypes: DatumField[ListDatum[PrimitiveTypeRef]] = field("supertypes", PrimitiveTypeRef.listOf)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_supertypes)

}


/**
 * {{{
 * vartype PrimitiveTypeRef extends DataTypeRef { override type: PrimitiveTypeData }
 * }}}
 */
trait PrimitiveTypeRef extends DataTypeRef with Var[PrimitiveTypeRef]


object PrimitiveTypeRef extends MultiVarType[PrimitiveTypeRef](ns \ "PrimitiveTypeRef", Seq(DataTypeRef)) {

  val _type: Tag[TypeRef.`"type"`.type, PrimitiveTypeData] = tag(TypeRef.`"type"`, PrimitiveTypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_type)

}


/**
 * {{{
 * record StringTypeData extends PrimitiveTypeData { override supertypes: list[StringTypeRef] }
 * }}}
 */
trait StringTypeData extends PrimitiveTypeData with RecordDatum[StringTypeData]


object StringTypeData extends RecordType[StringTypeData](ns \ "StringTypeData", Seq(PrimitiveTypeData), true) {

  val _supertypes: DatumField[ListDatum[StringTypeRef]] = field("supertypes", StringTypeRef.listOf)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_supertypes)

}


/**
 * {{{
 * vartype StringTypeRef extends PrimitiveTypeRef { override type: StringTypeData }
 * }}}
 */
trait StringTypeRef extends PrimitiveTypeRef with Var[StringTypeRef]


object StringTypeRef extends MultiVarType[StringTypeRef](ns \ "StringTypeRef", Seq(DataTypeRef)) {

  val _type: Tag[TypeRef.`"type"`.type, StringTypeData] = tag(TypeRef.`"type"`, StringTypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_type)

}


/**
 * {{{
 * record IntegerTypeData extends PrimitiveTypeData { override supertypes: list[IntegerTypeRef] }
 * }}}
 */
trait IntegerTypeData extends PrimitiveTypeData with RecordDatum[IntegerTypeData]


object IntegerTypeData extends RecordType[IntegerTypeData](ns \ "IntegerTypeData", Seq(PrimitiveTypeData), true) {

  val _supertypes: DatumField[ListDatum[IntegerTypeRef]] = field("supertypes", IntegerTypeRef.listOf)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_supertypes)

}


/**
 * {{{
 * vartype IntegerTypeRef extends PrimitiveTypeRef { override type: IntegerTypeData }
 * }}}
 */
trait IntegerTypeRef extends PrimitiveTypeRef with Var[IntegerTypeRef]


object IntegerTypeRef extends MultiVarType[IntegerTypeRef](ns \ "IntegerTypeRef", Seq(DataTypeRef)) {

  val _type: Tag[TypeRef.`"type"`.type, IntegerTypeData] = tag(TypeRef.`"type"`, IntegerTypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_type)

}


/**
 * {{{
 * record LongTypeData extends PrimitiveTypeData { override supertypes: list[LongTypeRef] }
 * }}}
 */
trait LongTypeData extends PrimitiveTypeData with RecordDatum[LongTypeData]


object LongTypeData extends RecordType[LongTypeData](ns \ "LongTypeData", Seq(PrimitiveTypeData), true) {

  val _supertypes: DatumField[ListDatum[LongTypeRef]] = field("supertypes", LongTypeRef.listOf)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_supertypes)

}


/**
 * {{{
 * vartype LongTypeRef extends PrimitiveTypeRef { override type: LongTypeData }
 * }}}
 */
trait LongTypeRef extends PrimitiveTypeRef with Var[LongTypeRef]


object LongTypeRef extends MultiVarType[LongTypeRef](ns \ "LongTypeRef", Seq(DataTypeRef)) {

  val _type: Tag[TypeRef.`"type"`.type, LongTypeData] = tag(TypeRef.`"type"`, LongTypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_type)

}


/**
 * {{{
 * record DoubleTypeData extends PrimitiveTypeData { override supertypes: list[DoubleTypeRef] }
 * }}}
 */
trait DoubleTypeData extends PrimitiveTypeData with RecordDatum[DoubleTypeData]


object DoubleTypeData extends RecordType[DoubleTypeData](ns \ "DoubleTypeData", Seq(PrimitiveTypeData), true) {

  val _supertypes: DatumField[ListDatum[DoubleTypeRef]] = field("supertypes", DoubleTypeRef.listOf)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_supertypes)

}


/**
 * {{{
 * vartype DoubleTypeRef extends PrimitiveTypeRef { override type: DoubleTypeData }
 * }}}
 */
trait DoubleTypeRef extends PrimitiveTypeRef with Var[DoubleTypeRef]


object DoubleTypeRef extends MultiVarType[DoubleTypeRef](ns \ "DoubleTypeRef", Seq(DataTypeRef)) {

  val _type: Tag[TypeRef.`"type"`.type, DoubleTypeData] = tag(TypeRef.`"type"`, DoubleTypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_type)

}


/**
 * {{{
 * record BooleanTypeData extends PrimitiveTypeData { override supertypes: list[BooleanTypeRef] }
 * }}}
 */
trait BooleanTypeData extends PrimitiveTypeData with RecordDatum[BooleanTypeData]


object BooleanTypeData extends RecordType[BooleanTypeData](ns \ "BooleanTypeData", Seq(PrimitiveTypeData), true) {

  val _supertypes: DatumField[ListDatum[BooleanTypeRef]] = field("supertypes", BooleanTypeRef.listOf)

  override lazy val declaredFields: DeclaredFields = DeclaredFields(_supertypes)

}


/**
 * {{{
 * vartype BooleanTypeRef extends PrimitiveTypeRef { override type: BooleanTypeData }
 * }}}
 */
trait BooleanTypeRef extends PrimitiveTypeRef with Var[BooleanTypeRef]


object BooleanTypeRef extends MultiVarType[BooleanTypeRef](ns \ "BooleanTypeRef", Seq(DataTypeRef)) {

  val _type: Tag[TypeRef.`"type"`.type, BooleanTypeData] = tag(TypeRef.`"type"`, BooleanTypeData)

  override lazy val declaredVarTags: DeclaredTags = declareTags(_type)

}

