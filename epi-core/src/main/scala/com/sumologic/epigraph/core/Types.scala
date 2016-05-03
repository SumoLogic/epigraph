/* Created by yegor on 4/25/16. */

package com.sumologic.epigraph.core

import scala.language.implicitConversions

trait Types {this: Names =>

  type Type >: Null <: AnyRef with TypeApi

  type VarType >: Null <: Type with VarTypeApi

  type TypeMember >: Null <: AnyRef with TypeMemberApi // TODO rename to VarMember?

  type DataType >: Null <: Type with DataTypeApi

  type RecordDataType >: Null <: DataType with RecordDataTypeApi

  type Field >: Null <: AnyRef with FieldApi // TODO common parent for Field, Tag? and Field, Tag, List, Map (re valueType)??

  type UnionDataType >: Null <: DataType with UnionDataTypeApi

  type Tag >: Null <: AnyRef with TagApi // TODO common parent for Field, Tag? and Field, Tag, List, Map (re value type)??

  type MapDataType >: Null <: DataType with MapDataTypeApi

  type ListDataType >: Null <: DataType with ListDataTypeApi

  type EnumDataType >: Null <: DataType with EnumDataTypeApi

  type EnumTypeMember >: Null <: AnyRef with EnumTypeMemberApi

  type PrimitiveDataType >: Null <: DataType with PrimitiveDataTypeApi

  type StringDataType >: Null <: PrimitiveDataType with StringDataTypeApi


  trait TypeApi extends Named {this: Type =>

    override def name: QualifiedTypeName

  }


  trait VarTypeApi extends TypeApi {this: VarType =>

    def defaultMember: Option[TypeMember]

    def members: Seq[TypeMember]

    def listOf: ListDataType // TODO move to TypeApi?

  }


  object VarTypeApi {

    implicit def dataTypeToVarType(dataType: DataType): VarType = dataType.varType

  }


  trait TypeMemberApi extends Named {this: TypeMember =>

    def name: TypeMemberName // TODO Option[] or need to infer anonymous var type names?

    def dataType: DataType

  }


  trait DataTypeApi extends TypeApi {this: DataType =>

    def supertypes: Seq[DataType]

    def varType: VarType

    //def listOf: ListDataType

  }


  trait RecordDataTypeApi extends DataTypeApi {this: RecordDataType =>

    override def supertypes: Seq[RecordDataType]

    def declaredFields: Seq[Field]

  }


  trait FieldApi extends Named {this: Field =>

    def name: FieldName

    def valueType: VarType

  }


  trait UnionDataTypeApi extends DataTypeApi {this: UnionDataType =>

    override val supertypes: Seq[UnionDataType] = Types.EmptySeq

    def declaredTags: Seq[Tag]

  }


  trait TagApi extends Named {this: Tag =>

    def name: TagName

    def valueType: VarType

  }


  trait MapDataTypeApi extends DataTypeApi {this: MapDataType =>

    override def supertypes: Seq[MapDataType]

    def keyType: DataType

    def valueType: VarType

  }


  trait ListDataTypeApi extends DataTypeApi {this: ListDataType =>

    override def supertypes: Seq[ListDataType]

    def valueType: VarType

  }


  trait EnumDataTypeApi extends DataTypeApi {this: EnumDataType =>

    override val supertypes: Seq[EnumDataType] = Types.EmptySeq

    def members: Seq[EnumTypeMember] // TODO map?

  }


  trait EnumTypeMemberApi extends Named {this: EnumTypeMember =>

    def name: EnumMemberName

  }


  trait PrimitiveDataTypeApi extends DataTypeApi {this: PrimitiveDataType =>

    override def supertypes: Seq[PrimitiveDataType]

  }


  trait StringDataTypeApi extends PrimitiveDataTypeApi {this: StringDataType =>

    override def supertypes: Seq[StringDataType]

  }

  // TODO other primitive types

}


object Types {

  val EmptySeq: Seq[Nothing] = Seq.empty // TODO move to util or smth.

}