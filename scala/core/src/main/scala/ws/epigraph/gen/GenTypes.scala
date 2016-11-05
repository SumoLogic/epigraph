/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 4/25/16. */

package ws.epigraph.gen

trait GenTypes {this: GenNames =>

  // TODO rename all BlahDataTypeApi to BlahTypeApi

  type GenType >: Null <: AnyRef with TypeApi

  type GenVarType >: Null <: GenType with VarTypeApi

  type GenTypeMember >: Null <: AnyRef with TypeMemberApi // TODO rename to GenVarMember? GenTypeVariant?

  type GenDataType >: Null <: GenType with DataTypeApi with VarTypeApi

  type GenRecordType >: Null <: GenDataType with RecordDataTypeApi

  type GenField >: Null <: AnyRef with FieldApi // TODO common parent for Field, Tag? and Field, Tag, List, Map (re valueType)??

  type GenUnionType >: Null <: GenDataType with UnionDataTypeApi

  type GenTag >: Null <: AnyRef with TagApi // TODO common parent for Field, Tag? and Field, Tag, List, Map (re value type)??

  type GenMapType >: Null <: GenDataType with MapDataTypeApi

  type GenListType >: Null <: GenDataType with ListDataTypeApi

  type GenEnumType >: Null <: GenDataType with EnumDataTypeApi

  type GenEnumTypeMember >: Null <: AnyRef with EnumTypeMemberApi

  type GenPrimitiveType >: Null <: GenDataType with PrimitiveDataTypeApi

  type GenStringType >: Null <: GenPrimitiveType with StringDataTypeApi


  trait TypeApi extends Named[QualifiedTypeName] {this: GenType =>

    def supertypes: Seq[GenType]

  }


  trait VarTypeApi extends TypeApi {this: GenVarType =>

    override def supertypes: Seq[GenVarType]

    @deprecated("Probably don't need this one?", "since")
    def defaultMember: Option[GenTypeMember]

    def members: Seq[GenTypeMember]

    def listOf: GenListType // TODO move to TypeApi?

    // TODO def mapOf...

  }


  trait TypeMemberApi extends Named[TypeMemberName] {this: GenTypeMember =>

    // TODO Option[Name] or need to infer anonymous var type names?

    def dataType: GenDataType

  }


  trait DataTypeApi extends TypeApi {this: GenDataType =>

    override def supertypes: Seq[GenDataType]

    //def varType: GenVarType // FIXME should extend VarTypeApi (or common super) instead

  }


  trait RecordDataTypeApi extends DataTypeApi {this: GenRecordType =>

    override def supertypes: Seq[GenRecordType]

    def declaredFields: Seq[GenField]

  }


  trait FieldApi extends Named[FieldName] {this: GenField =>

    def varType: GenVarType

  }


  trait UnionDataTypeApi extends DataTypeApi {this: GenUnionType => // TODO add UnionValueTypeApi?

    override val supertypes: Seq[GenUnionType] = Nil // TODO: non-empty Seq[GenVarType]? separate superVarTypes?

    def declaredTags: Seq[GenTag]

  }


  trait TagApi extends Named[TagName] {this: GenTag =>

    def valueType: GenVarType

  }


  trait MapDataTypeApi extends DataTypeApi {this: GenMapType =>

    override def supertypes: Seq[GenMapType]

    def keyType: GenDataType

    def valueType: GenVarType

  }


  trait ListDataTypeApi extends DataTypeApi {this: GenListType =>

    override def supertypes: Seq[GenListType]

    def valueVarType: GenVarType

  }


  trait EnumDataTypeApi extends DataTypeApi {this: GenEnumType =>

    override val supertypes: Seq[GenEnumType] = Nil

    def values: Seq[GenEnumTypeMember] // TODO map?

  }


  trait EnumTypeMemberApi extends Named[EnumValueName] {this: GenEnumTypeMember =>}


  trait PrimitiveDataTypeApi extends DataTypeApi {this: GenPrimitiveType =>

    override def supertypes: Seq[GenPrimitiveType]

  }


  trait StringDataTypeApi extends PrimitiveDataTypeApi {this: GenStringType =>

    override def supertypes: Seq[GenStringType]

  }

  // TODO other primitive types

}
