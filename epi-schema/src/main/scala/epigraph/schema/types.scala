/* Created by yegor on 5/23/16. */

package epigraph.schema

import com.sumologic.epigraph.std
import com.sumologic.epigraph.xp._
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

  val `"name"`: std.TypeMemberName = "name"

  val _name: Tag[`"name"`.type, Name] = tag(`"name"`, Name)

  override def declaredVarTags: DeclaredTags = DeclaredTags(_name)

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

  val `"type"`: std.TypeMemberName = "type"

  val _type: Tag[`"type"`.type, TypeData] = tag(`"type"`, TypeData)

  override def declaredVarTags: DeclaredTags = DeclaredTags(_name, _type)

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

  override def declaredVarTags: DeclaredTags = DeclaredTags(_type)

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

  val `"member"`: std.TypeMemberName = "member"

  val _member: Tag[`"member"`.type, TypeMemberData] = tag(`"member"`, TypeMemberData)

  override def declaredVarTags: DeclaredTags = DeclaredTags(_name, _member)

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


object DataTypeData extends RecordType[DataTypeData](ns \ "DataTypeData", Seq(TypeData)) {

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

  override def declaredVarTags: DeclaredTags = DeclaredTags(_type)

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

  override lazy val declaredVarTags: DeclaredTags = DeclaredTags(_type)

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
