/* Created by yegor on 4/25/16. */

package com.sumologic.epigraph.core

trait Types {self: Nature =>


  trait Named {

    def name: Name

  }


  type Type >: Null <: AnyRef with TypeApi


  trait TypeApi extends Named {this: Type =>

    def name: QualifiedTypeName

  }


  /**
   * @tparam D default member type
   */
  type VarValueType[D >: Null <: AnyRef] >: Null <: Type with VarValueTypeApi[D] // TODO not sure we need this type member, Api could be enough

  /**
   * @tparam D default member type
   */
  trait VarValueTypeApi[D >: Null <: AnyRef] extends TypeApi {this: VarValueType[D] =>

    def defaultMember: D

    def members: Map[TypeMemberName, TypeMember]

  }


  type TypeMember >: Null <: AnyRef with TypeMemberApi


  trait TypeMemberApi extends Named {this: TypeMember =>

    def name: TypeMemberName // TODO Option[] or need to infer anonymous var type names?

    def `type`: DataType

  }


  type VarType >: Null <: VarValueType[Option[TypeMember]] with VarTypeApi


  trait VarTypeApi extends VarValueTypeApi[Option[TypeMember]] {this: VarType =>}


  type ValueType >: Null <: VarValueType[TypeMember] with ValueTypeApi


  trait ValueTypeApi extends VarValueTypeApi[TypeMember] {this: ValueType =>}


  type DataType >: Null <: Type with DataTypeApi


  trait DataTypeApi extends TypeApi {this: DataType =>

    def supertypes: Seq[DataType]

  }


  type RecordDataType >: Null <: DataType with RecordDataTypeApi


  trait RecordDataTypeApi extends DataTypeApi {this: RecordDataType =>

    override def supertypes: Seq[RecordDataType]

    def declaredFields: Map[FieldName, Field]

  }


  type Field >: Null <: AnyRef with FieldApi // TODO common parent for Field, Tag? and Field, Tag, List, Map (re value type)??

  trait FieldApi extends Named {this: Field =>

    def name: FieldName

    def `type`: ValueType // TODO valueType?

  }


  type UnionDataType >: Null <: DataType with UnionDataTypeApi


  trait UnionDataTypeApi extends DataTypeApi {this: UnionDataType =>

    override def supertypes: Seq[UnionDataType]

    def declaredTags: Map[TagName, Tag]

  }


  type Tag >: Null <: AnyRef with TagApi // TODO common parent for Field, Tag? and Field, Tag, List, Map (re value type)??

  trait TagApi extends Named {this: Tag =>

    def name: TagName

    def valueType: ValueType

  }


  type MapDataType >: Null <: DataType with MapDataTypeApi


  trait MapDataTypeApi extends DataTypeApi {this: MapDataType =>

    override def supertypes: Seq[MapDataType]

    def keyType: DataType

    def valueType: ValueType

  }


  type ListDataType >: Null <: DataType with ListDataTypeApi


  trait ListDataTypeApi extends DataTypeApi {this: ListDataType =>

    override def supertypes: Seq[ListDataType]

    def valueType: ValueType

  }


  type EnumDataType >: Null <: DataType with EnumDataTypeApi


  trait EnumDataTypeApi extends DataTypeApi {this: EnumDataType =>

    override val supertypes: Seq[EnumDataType] = Seq.empty

    def members: Seq[EnumTypeMember] // TODO map?

  }


  type EnumTypeMember >: Null <: AnyRef with EnumTypeMemberApi


  trait EnumTypeMemberApi extends Named {this: EnumTypeMember =>

    def name: EnumMemberName

  }


  type PrimitiveDataType >: Null <: DataType with PrimitiveDataTypeApi


  trait PrimitiveDataTypeApi extends DataTypeApi {this: PrimitiveDataType =>

    override def supertypes: Seq[PrimitiveDataType]

  }


  type StringDataType >: Null <: PrimitiveDataType with StringDataTypeApi


  trait StringDataTypeApi extends PrimitiveDataTypeApi {this: StringDataType =>

    override def supertypes: Seq[StringDataType]

  }

  // TODO other primitive types


}
