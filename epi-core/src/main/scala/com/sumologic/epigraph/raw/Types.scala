/* Created by yegor on 4/28/16. */

package com.sumologic.epigraph.raw

import com.sumologic.epigraph.core

trait Types extends core.Types {this: core.Names =>

  override type Type = RawType

  override type VarType = RawVarType

  override type TypeMember = RawTypeMember

  override type DataType = RawDataType

  override type RecordDataType = RawRecordDataType

  override type Field = RawField

  override type UnionDataType = RawUnionDataType

  override type Tag = RawTag

  override type MapDataType = RawMapDataType

  override type ListDataType = RawListDataType

  override type EnumDataType = RawEnumDataType

  override type EnumTypeMember = RawEnumTypeMember

  override type PrimitiveDataType = RawPrimitiveDataType

  override type StringDataType = RawStringDataType


  abstract class RawType(private val tname: QualifiedTypeName) extends TypeApi {

    override def name: QualifiedTypeName = tname

  }


  class RawVarType(
      name: QualifiedTypeName,
      override val defaultMember: Option[TypeMember],
      membersSeq: Seq[TypeMember] // TODO add method
  ) extends Type(name) with VarTypeApi {

    // TODO parameterized method to return any supported view of this?
    override def membersMap: Map[TypeMemberName, TypeMember] = ???

  }


  class RawTypeMember(private val mname: TypeMemberName, override val dataType: DataType) extends TypeMemberApi {

    override def name: TypeMemberName = mname

  }


  abstract class RawDataType(name: QualifiedTypeName, override val supertypes: Seq[DataType]) extends Type(
    name
  ) with DataTypeApi


  class RawRecordDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[RecordDataType],
      override val declaredFields: Map[FieldName, Field]
  ) extends DataType(name, supertypes) with RecordDataTypeApi


  class RawField(private val fname: FieldName, override val valueType: VarType) extends FieldApi {

    override def name: FieldName = fname

  }


  class RawUnionDataType(
      name: QualifiedTypeName,
      supertypes: Seq[UnionDataType],
      override val declaredTags: Map[TagName, Tag]
  ) extends DataType(name, supertypes) with UnionDataTypeApi


  class RawTag(private val tname: TagName, override val valueType: VarType) extends TagApi {

    override def name: TagName = tname

  }


  class RawMapDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[MapDataType],
      override val keyType: DataType,
      override val valueType: VarType
  ) extends DataType(name, supertypes) with MapDataTypeApi


  class RawListDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[ListDataType],
      override val valueType: VarType
  ) extends DataType(name, supertypes) with ListDataTypeApi


  class RawEnumDataType(
      name: QualifiedTypeName,
      override val members: Seq[EnumTypeMember]
  ) extends DataType(name, core.Types.EmptySeq) with EnumDataTypeApi


  class RawEnumTypeMember(private val ename: EnumMemberName) extends EnumTypeMemberApi {

    override def name: EnumMemberName = ename

  }


  class RawPrimitiveDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[PrimitiveDataType]
  ) extends DataType(name, supertypes) with PrimitiveDataTypeApi


  class RawStringDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[StringDataType]
  ) extends PrimitiveDataType(name, supertypes) with StringDataTypeApi


}
