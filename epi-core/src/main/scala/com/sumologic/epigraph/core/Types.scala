/* Created by yegor on 4/25/16. */

package com.sumologic.epigraph.core

trait Types {this: Names =>

  type Type >: Null <: AnyRef with TypeApi


  trait TypeApi extends Named {this: Type =>

    override def name: QualifiedTypeName

  }


  type VarType >: Null <: Type with VarTypeApi


  trait VarTypeApi extends TypeApi {this: VarType =>

    def defaultMember: Option[TypeMember]

    def membersMap: Map[TypeMemberName, TypeMember]

  }


  type TypeMember >: Null <: AnyRef with TypeMemberApi // TODO rename to VarMember?

  trait TypeMemberApi extends Named {this: TypeMember =>

    def name: TypeMemberName // TODO Option[] or need to infer anonymous var type names?

    def dataType: DataType

  }


  type DataType >: Null <: Type with DataTypeApi


  trait DataTypeApi extends TypeApi {this: DataType =>

    def supertypes: Seq[DataType]

  }


  type RecordDataType >: Null <: DataType with RecordDataTypeApi


  trait RecordDataTypeApi extends DataTypeApi {this: RecordDataType =>

    override def supertypes: Seq[RecordDataType]

    def declaredFields: Map[FieldName, Field]

  }


  type Field >: Null <: AnyRef with FieldApi // TODO common parent for Field, Tag? and Field, Tag, List, Map (re valueType)??

  trait FieldApi extends Named {this: Field =>

    def name: FieldName

    def valueType: VarType

  }


  type UnionDataType >: Null <: DataType with UnionDataTypeApi


  trait UnionDataTypeApi extends DataTypeApi {this: UnionDataType =>

    override val supertypes: Seq[UnionDataType] = Types.EmptySeq

    def declaredTags: Map[TagName, Tag]

  }


  type Tag >: Null <: AnyRef with TagApi // TODO common parent for Field, Tag? and Field, Tag, List, Map (re value type)??

  trait TagApi extends Named {this: Tag =>

    def name: TagName

    def valueType: VarType

  }


  type MapDataType >: Null <: DataType with MapDataTypeApi


  trait MapDataTypeApi extends DataTypeApi {this: MapDataType =>

    override def supertypes: Seq[MapDataType]

    def keyType: DataType

    def valueType: VarType

  }


  type ListDataType >: Null <: DataType with ListDataTypeApi


  trait ListDataTypeApi extends DataTypeApi {this: ListDataType =>

    override def supertypes: Seq[ListDataType]

    def valueType: VarType

  }


  type EnumDataType >: Null <: DataType with EnumDataTypeApi


  trait EnumDataTypeApi extends DataTypeApi {this: EnumDataType =>

    override val supertypes: Seq[EnumDataType] = Types.EmptySeq

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


object Types {

  val EmptySeq: Seq[Nothing] = Seq.empty

}