/* Created by yegor on 4/28/16. */

package ws.epigraph.raw

import ws.epigraph.gen.{GenNames, GenTypes}

trait RawTypes extends GenTypes {this: GenNames =>

  override type GenType = TypeApi

  override type GenVarType = VarTypeApi

  override type GenTypeMember = TypeMember

  override type GenDataType = DataType

  override type GenRecordType = RecordType

  override type GenField = Field

  override type GenUnionType = UnionType

  override type GenTag = Tag

  override type GenMapType = MapType

  override type GenListType = ListType

  override type GenEnumType = EnumType

  override type GenEnumTypeMember = EnumTypeMember

  override type GenPrimitiveType = PrimitiveType

  override type GenStringType = StringType


  abstract class Type(private val tname: QualifiedTypeName) extends TypeApi {

    override def name: QualifiedTypeName = tname

  }


  class VarType(
      override val name: QualifiedTypeName,
      override val supertypes: Seq[GenVarType],
      override val defaultMember: Option[TypeMember],
      override val members: Seq[TypeMember]
  ) extends Type(name) with VarTypeApi {

    // TODO parameterized method to return any supported view of this?

    override lazy val listOf: ListType = new ListType(name.listOf, Nil, this)

  }


  trait DefaultVarType extends VarTypeApi {this: DataType =>

    lazy val default: TypeMember = new TypeMember(TypeMemberName.default, this)

    override lazy val defaultMember: Option[TypeMember] = Some(default)

    override lazy val members: Seq[TypeMember] = Seq(default)

    override lazy val listOf: ListType = new ListType(name.listOf, Nil/*TODO all supertypes.listOf?*/ , this)
  }


  class TypeMember(private val mname: TypeMemberName, override val dataType: DataType) extends TypeMemberApi {

    override def name: TypeMemberName = mname

  }


  object TypeMember {

    def apply(name: TypeMemberName, dataType: DataType): TypeMember = new TypeMember(name, dataType)

  }


  abstract class DataType(name: QualifiedTypeName, override val supertypes: Seq[DataType]) extends Type(
    name
  ) with DataTypeApi with DefaultVarType {

  }

  class RecordType(
      name: QualifiedTypeName,
      override val supertypes: Seq[RecordType],
      declaredFieldsRef: => Seq[Field]
  ) extends DataType(name, supertypes) with RecordDataTypeApi {

    def this(name: QualifiedTypeName, supertype: RecordType, declaredFieldsRef: => Seq[Field]) = this(
      name, Seq(supertype), declaredFieldsRef
    )

    override lazy val declaredFields: Seq[Field] = declaredFieldsRef

  }


  class Field(private val fname: FieldName, varTypeRef: => GenVarType) extends FieldApi {

    override def name: FieldName = fname

    override lazy val varType: GenVarType = varTypeRef

  }


  object Field {

    def apply(name: FieldName, valueType: => GenVarType): Field = new Field(name, valueType)

  }


  class UnionType(
      name: QualifiedTypeName,
      override val declaredTags: Seq[Tag]
  ) extends DataType(name, Nil) with UnionDataTypeApi


  // TODO move inside UnionDataType (like type member since there's no inheritance/sharing)?
  class Tag(private val tname: TagName, valueTypeRef: => GenVarType) extends TagApi {

    override def name: TagName = tname

    override lazy val valueType: GenVarType = valueTypeRef

  }


  object Tag {

    def apply(name: TagName, valueType: => GenVarType): Tag = new Tag(name, valueType)

  }


  class MapType(
      name: QualifiedTypeName,
      override val supertypes: Seq[MapType],
      override val keyType: DataType,
      override val valueType: GenVarType
  ) extends DataType(name, supertypes) with MapDataTypeApi


  class ListType(
      name: QualifiedTypeName,
      override val supertypes: Seq[ListType],
      override val valueVarType: GenVarType
  ) extends DataType(name, supertypes) with ListDataTypeApi


  class EnumType(
      name: QualifiedTypeName,
      override val values: Seq[EnumTypeMember]
  ) extends DataType(name, Nil) with EnumDataTypeApi


  class EnumTypeMember(private val ename: EnumValueName) extends EnumTypeMemberApi {

    override def name: EnumValueName = ename

  }


  abstract class PrimitiveType(
      name: QualifiedTypeName,
      override val supertypes: Seq[PrimitiveType]
  ) extends DataType(name, supertypes) with PrimitiveDataTypeApi


  class StringType(
      name: QualifiedTypeName,
      override val supertypes: Seq[StringType]
  ) extends PrimitiveType(name, supertypes) with StringDataTypeApi {

    def this(name: QualifiedTypeName, supertype: StringType) = this(name, Seq(supertype))

  }


}
