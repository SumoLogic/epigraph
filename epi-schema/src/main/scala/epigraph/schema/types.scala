/* Created by yegor on 5/23/16. */

package epigraph.schema

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


/** {{{vartype ByNameRef default name}}} */
object ByNameRef extends MultiVarType[ByNameRef](ns \ "ByNameRef") {

  /* ```name: Name```*/
  val _name: VarTag[ByNameRef, Name] = declareTag("name", Name)

  override def declaredVarTags: Seq[VarTag[ByNameRef, _]] = Seq(_name)

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

  val _name: TaggedField[TypeData, QualifiedTypeName, QualifiedTypeName] = declareField("name", QualifiedTypeName)

  val _supertypes: TaggedField[TypeData, ListDatum[TypeRef], ListDatum[TypeRef]] = declareField(
    "supertypes", TypeRef.listOf
  )

  override def declaredFields: Seq[Field[TypeData, _]] = Seq[Field[TypeData, _]](_name, _supertypes)

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


/* ```vartype TypeRef extends ByNameRef``` */
object TypeRef extends MultiVarType[TypeRef](ns \ "TypeRef", Seq(ByNameRef)) {

  /* ```override name: QualifiedTypeName``` */
  val _name: VarTag[TypeRef, QualifiedTypeName] = declareTag("name", QualifiedTypeName)

  /* ```type: TypeData``` */
  val _type: VarTag[TypeRef, TypeData] = declareTag("type", TypeData)

  override def declaredVarTags: Seq[VarTag[TypeRef, _]] = Seq[VarTag[TypeRef, _]](_name, _type)

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

  /** ```override supertypes: list[VarTypeRef]``` */
  val _supertypes: TaggedField[VarTypeData, ListDatum[VarTypeRef], ListDatum[VarTypeRef]] = declareField(
    "supertypes", VarTypeRef.listOf
  )

  /** ````default`: TypeMemberRef``` */
  val _default: TaggedField[VarTypeData, TypeMemberRef, TypeMemberName] = declareField(
    "default", TypeMemberRef, TypeMemberRef._name
  )

  /** ```members: list[TypeMemberData]``` */
  val _members: TaggedField[VarTypeData, ListDatum[TypeMemberData], ListDatum[TypeMemberData]] = declareField(
    "members", TypeMemberData.listOf
  )

  override def declaredFields: Seq[Field[VarTypeData, _]] = Seq[Field[VarTypeData, _]](_supertypes, _default, _members)

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

  val _name: VarTag[TypeRef, QualifiedTypeName] = TypeRef._name

  val _type: VarTag[VarTypeRef, VarTypeData] = declareTag("type", VarTypeData)

  override def declaredVarTags: Seq[VarTag[VarTypeRef, _]] = Seq(_type)

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

  val _name: TaggedField[TypeMemberData, TypeMemberName, TypeMemberName] = declareField("name", TypeMemberName)

  val _dataType: TaggedField[TypeMemberData, DataTypeRef, QualifiedTypeName] = declareField[DataTypeRef, QualifiedTypeName](
    "dataType", DataTypeRef, DataTypeRef._name
  )

  override def declaredFields: Seq[Field[TypeMemberData, _]] = Seq[Field[TypeMemberData, _]](_name, _dataType)

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

  val _name: VarTag[TypeMemberRef, TypeMemberName] = declareTag("name", TypeMemberName)

  val _member: VarTag[TypeMemberRef, TypeMemberData] = declareTag("member", TypeMemberData)

  override def declaredVarTags: Seq[VarTag[TypeMemberRef, _]] = Seq[VarTag[TypeMemberRef, _]](_name, _member)

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

  val _supertypes: TaggedField[DataTypeData, ListDatum[DataTypeRef], ListDatum[DataTypeRef]] = declareField(
    "supertypes", DataTypeRef.listOf
  )

  val _polymorphic: TaggedField[DataTypeData, BooleanPrimitive, BooleanPrimitive] = declareField(
    "polymorphic", BooleanPrimitive
  )

  val _metaType: TaggedField[DataTypeData, DataTypeRef, QualifiedTypeName] = declareField[DataTypeRef, QualifiedTypeName](
    "metaType", DataTypeRef, DataTypeRef._name
  )

  override def declaredFields: Seq[Field[DataTypeData, _]] = Seq[Field[DataTypeData, _]](
    _supertypes, _polymorphic, _metaType
  )

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

  val _name: VarTag[TypeRef, QualifiedTypeName] = TypeRef._name

  val _type: VarTag[DataTypeRef, DataTypeData] = declareTag("type", DataTypeData)

  override def declaredVarTags: Seq[VarTag[DataTypeRef, _]] = Seq(_type)

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

  val _supertypes: TaggedField[RecordTypeData, ListDatum[RecordTypeRef], ListDatum[RecordTypeRef]] = declareField(
    "supertypes", RecordTypeRef.listOf
  )

  val _declaredFields: TaggedField[RecordTypeData, ListDatum[FieldData], ListDatum[FieldData]] = declareField(
    "declaredFields", FieldData.listOf
  )

  override def declaredFields: Seq[Field[RecordTypeData, _]] = Seq[Field[RecordTypeData, _]](
    _supertypes, _declaredFields
  )

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

  val _name: VarTag[TypeRef, QualifiedTypeName] = TypeRef._name

  val _type: VarTag[RecordTypeRef, RecordTypeData] = declareTag("type", RecordTypeData)

  override def declaredVarTags: Seq[VarTag[RecordTypeRef, _]] = Seq[VarTag[RecordTypeRef, _]](_type)

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

  val _name: TaggedField[FieldData, FieldName, FieldName] = declareField("name", FieldName)

  val _valueType: TaggedField[FieldData, VarTypeRef, QualifiedTypeName] = declareField[VarTypeRef, QualifiedTypeName](
    "valueType", VarTypeRef, VarTypeRef._name
  )

  val _default: TaggedField[FieldData, TypeMemberRef, TypeMemberName] = declareField(
    "default", TypeMemberRef, TypeMemberRef._name
  )

  override def declaredFields: Seq[Field[FieldData, _]] = Seq[Field[FieldData, _]](_name, _valueType, _default)

}
