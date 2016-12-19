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

/* Created by yegor on 5/2/16. */

package ws.epigraph.schema

import ws.epigraph.raw.{RawNames, RawTypes}

trait RawBuiltins {this: RawNames with RawTypes => // TODO move to separate file (package?)

  val BuiltinNamespaceName = new QualifiedNamespaceName(None, new LocalNamespaceName("epigraph"))

  val StringType = new GenStringType(BuiltinNamespaceName \ "String", Nil)

}


trait RawSchemaNamesSchema {this: RawNames with RawTypes with RawBuiltins =>

  val SchemaNamespaceName: QualifiedNamespaceName = BuiltinNamespaceName / "schema"


  object NameType extends GenStringType(SchemaNamespaceName \ "Name", StringType)


  object LocalNameType extends GenStringType(SchemaNamespaceName \ "LocalName", NameType)


  object QualifiedNameType extends GenStringType(SchemaNamespaceName \ "QualifiedName", NameType)


  object LocalNamespaceNameType extends GenStringType(SchemaNamespaceName \ "LocalNamespaceName", LocalNameType)


  object QualifiedNamespaceNameType extends GenStringType(
    SchemaNamespaceName \ "QualifiedNamespaceName", QualifiedNameType
  )


  object LocalTypeNameType extends GenStringType(SchemaNamespaceName \ "LocalTypeName", LocalNameType)


  object QualifiedTypeNameType extends GenStringType(SchemaNamespaceName \ "QualifiedTypeName", QualifiedNameType)


  object TypeMemberNameType extends GenStringType(SchemaNamespaceName \ "TypeMemberName", LocalNameType)


  object FieldNameType extends GenStringType(SchemaNamespaceName \ "FieldName", LocalNameType)


  object TagNameType extends GenStringType(SchemaNamespaceName \ "TagName", LocalNameType)


  object EnumValueNameType extends GenStringType(SchemaNamespaceName \ "EnumValueName", LocalNameType)


}


trait RawSchemaTypesSchema {this: RawNames with RawTypes with RawBuiltins with RawSchemaNamesSchema =>


  object TypeType extends {

    val Name = Field("name", QualifiedTypeNameType)

  } with GenRecordType(SchemaNamespaceName \ "Type", Nil, Seq(Name))


  object VarTypeType extends {

    val Default = Field("default", TypeMemberNameType) // TODO ref? +ref?

    val Members = Field("members", TypeMemberType.listOf)

  } with GenRecordType(SchemaNamespaceName \ "VarType", TypeType, Seq(Default, Members))


  object TypeMemberType extends {

    val Name = Field("name", TypeMemberNameType)

    val DataType = Field("dataType", DataTypeRefType)

  } with GenRecordType(SchemaNamespaceName \ "TypeMember", Nil, Seq(Name, DataType))


  object VarTypeRefType extends VarType(
    SchemaNamespaceName \ "VarTypeRef",
    Nil,
    Some(VarTypeRefMembers.Name),
    Seq(VarTypeRefMembers.Name, VarTypeRefMembers.Type)
  )


  object VarTypeRefMembers {

    val Name = TypeMember("name", QualifiedTypeNameType)

    val Type = TypeMember("type", VarTypeType)

  }


  object DataTypeRefType extends VarType(
    SchemaNamespaceName \ "DataTypeRef",
    Nil,
    Some(DataTypeRefMembers.Name),
    Seq(DataTypeRefMembers.Name, DataTypeRefMembers.Type)
  )


  object DataTypeRefMembers {

    val Name = TypeMember("name", QualifiedTypeNameType)

    val Type = TypeMember("type", DataTypeUnionType)

  }


  object DataTypeUnionType extends {

    val Record = Tag("record", RecordDataTypeType)

    val Union = Tag("union", UnionDataTypeType)

    val Map = Tag("map", MapDataTypeType)

    val List = Tag("list", ListDataTypeType)

    val Enum = Tag("enum", EnumDataTypeType)

    val String = Tag("string", StringDataTypeType)

    // TODO other primitive types

  } with GenUnionType(SchemaNamespaceName \ "DataType", Seq(Record, Union, Map, List, Enum, String))


  object DataTypeType extends {

    val Supertypes = Field("supertypes", DataTypeRefType.listOf)

  } with GenRecordType(SchemaNamespaceName \ "DataType", TypeType, Seq(Supertypes))


  object RecordDataTypeType extends {

    val DeclaredFields = Field("declaredFields", FieldType.listOf)

  } with GenRecordType(SchemaNamespaceName \ "RecordDataType", DataTypeType, Seq(DeclaredFields))


  object FieldType extends {

    val Name = Field("name", FieldNameType)

    val ValueType = Field("valueType", VarTypeRefType)

  } with GenRecordType(SchemaNamespaceName \ "Field", Nil, Seq(Name, ValueType))


  object UnionDataTypeType extends {

    val Tags = Field("tags", TagType.listOf)

  } with GenRecordType(SchemaNamespaceName \ "UnionDataType", DataTypeType, Seq(Tags))


  object TagType extends {

    val Name = Field("name", TagNameType)

    val ValueType = Field("valueType", VarTypeRefType)

  } with GenRecordType(SchemaNamespaceName \ "Tag", Nil, Seq(Name, ValueType))


  object MapDataTypeType extends {

    val KeyType = Field("keyType", DataTypeRefType)

    val ValueType = Field("valueType", VarTypeRefType)

  } with GenRecordType(SchemaNamespaceName \ "MapDataType", DataTypeType, Seq(KeyType, ValueType))


  object ListDataTypeType extends {

    val ValueType = Field("valueType", VarTypeRefType)

  } with GenRecordType(SchemaNamespaceName \ "ListDataType", DataTypeType, Seq(ValueType))


  object EnumDataTypeType extends {

    val Values = Field("values", EnumValueType)

  } with GenRecordType(SchemaNamespaceName \ "EnumDataType", DataTypeType, Seq(Values))


  object EnumValueType extends {

    val Name = Field("name", EnumValueNameType)

  } with GenRecordType(SchemaNamespaceName \ "EnumValue", Nil, Seq(Name))


  object PrimitiveDataTypeType extends GenRecordType(SchemaNamespaceName \ "PrimitiveDataType", DataTypeType, Nil)


  object StringDataTypeType extends GenRecordType(
    SchemaNamespaceName \ "StringDataType", Seq(PrimitiveDataTypeType), Nil
  )


  val CircularFooType: RecordType = new RecordType(SchemaNamespaceName \ "Foo", Nil, Seq(FooFields.Foo, FooFields.Bar))


  object FooFields {

    val Foo = Field("foo", CircularFooType)

    val Bar = Field("bar", CircularBarType)

  }

  val CircularBarType: RecordType = new RecordType(SchemaNamespaceName \ "Bar", Nil, Seq(BarFields.Foo, BarFields.Bar))


  object BarFields {

    val Foo = Field("foo", CircularFooType)

    val Bar = Field("bar", CircularBarType)

  }


}


trait RawSchemaSchema extends RawNames with RawTypes with RawBuiltins with RawSchemaNamesSchema with RawSchemaTypesSchema


object RawSchemaTest extends RawSchemaSchema {

  def main(args: Array[String]) {
    println(SchemaNamespaceName)
    println(StringDataTypeType)
    println(CircularBarType)
    println(CircularFooType)
  }

}
