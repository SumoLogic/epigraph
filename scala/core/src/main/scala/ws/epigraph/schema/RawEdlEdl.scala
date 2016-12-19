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


trait RawEdlNamesEdl {this: RawNames with RawTypes with RawBuiltins =>

  val EdlNamespaceName: QualifiedNamespaceName = BuiltinNamespaceName / "schema"


  object NameType extends GenStringType(EdlNamespaceName \ "Name", StringType)


  object LocalNameType extends GenStringType(EdlNamespaceName \ "LocalName", NameType)


  object QualifiedNameType extends GenStringType(EdlNamespaceName \ "QualifiedName", NameType)


  object LocalNamespaceNameType extends GenStringType(EdlNamespaceName \ "LocalNamespaceName", LocalNameType)


  object QualifiedNamespaceNameType extends GenStringType(
    EdlNamespaceName \ "QualifiedNamespaceName", QualifiedNameType
  )


  object LocalTypeNameType extends GenStringType(EdlNamespaceName \ "LocalTypeName", LocalNameType)


  object QualifiedTypeNameType extends GenStringType(EdlNamespaceName \ "QualifiedTypeName", QualifiedNameType)


  object TypeMemberNameType extends GenStringType(EdlNamespaceName \ "TypeMemberName", LocalNameType)


  object FieldNameType extends GenStringType(EdlNamespaceName \ "FieldName", LocalNameType)


  object TagNameType extends GenStringType(EdlNamespaceName \ "TagName", LocalNameType)


  object EnumValueNameType extends GenStringType(EdlNamespaceName \ "EnumValueName", LocalNameType)


}


trait RawEdlTypesEdl {this: RawNames with RawTypes with RawBuiltins with RawEdlNamesEdl =>


  object TypeType extends {

    val Name = Field("name", QualifiedTypeNameType)

  } with GenRecordType(EdlNamespaceName \ "Type", Nil, Seq(Name))


  object VarTypeType extends {

    val Default = Field("default", TypeMemberNameType) // TODO ref? +ref?

    val Members = Field("members", TypeMemberType.listOf)

  } with GenRecordType(EdlNamespaceName \ "VarType", TypeType, Seq(Default, Members))


  object TypeMemberType extends {

    val Name = Field("name", TypeMemberNameType)

    val DataType = Field("dataType", DataTypeRefType)

  } with GenRecordType(EdlNamespaceName \ "TypeMember", Nil, Seq(Name, DataType))


  object VarTypeRefType extends VarType(
    EdlNamespaceName \ "VarTypeRef",
    Nil,
    Some(VarTypeRefMembers.Name),
    Seq(VarTypeRefMembers.Name, VarTypeRefMembers.Type)
  )


  object VarTypeRefMembers {

    val Name = TypeMember("name", QualifiedTypeNameType)

    val Type = TypeMember("type", VarTypeType)

  }


  object DataTypeRefType extends VarType(
    EdlNamespaceName \ "DataTypeRef",
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

  } with GenUnionType(EdlNamespaceName \ "DataType", Seq(Record, Union, Map, List, Enum, String))


  object DataTypeType extends {

    val Supertypes = Field("supertypes", DataTypeRefType.listOf)

  } with GenRecordType(EdlNamespaceName \ "DataType", TypeType, Seq(Supertypes))


  object RecordDataTypeType extends {

    val DeclaredFields = Field("declaredFields", FieldType.listOf)

  } with GenRecordType(EdlNamespaceName \ "RecordDataType", DataTypeType, Seq(DeclaredFields))


  object FieldType extends {

    val Name = Field("name", FieldNameType)

    val ValueType = Field("valueType", VarTypeRefType)

  } with GenRecordType(EdlNamespaceName \ "Field", Nil, Seq(Name, ValueType))


  object UnionDataTypeType extends {

    val Tags = Field("tags", TagType.listOf)

  } with GenRecordType(EdlNamespaceName \ "UnionDataType", DataTypeType, Seq(Tags))


  object TagType extends {

    val Name = Field("name", TagNameType)

    val ValueType = Field("valueType", VarTypeRefType)

  } with GenRecordType(EdlNamespaceName \ "Tag", Nil, Seq(Name, ValueType))


  object MapDataTypeType extends {

    val KeyType = Field("keyType", DataTypeRefType)

    val ValueType = Field("valueType", VarTypeRefType)

  } with GenRecordType(EdlNamespaceName \ "MapDataType", DataTypeType, Seq(KeyType, ValueType))


  object ListDataTypeType extends {

    val ValueType = Field("valueType", VarTypeRefType)

  } with GenRecordType(EdlNamespaceName \ "ListDataType", DataTypeType, Seq(ValueType))


  object EnumDataTypeType extends {

    val Values = Field("values", EnumValueType)

  } with GenRecordType(EdlNamespaceName \ "EnumDataType", DataTypeType, Seq(Values))


  object EnumValueType extends {

    val Name = Field("name", EnumValueNameType)

  } with GenRecordType(EdlNamespaceName \ "EnumValue", Nil, Seq(Name))


  object PrimitiveDataTypeType extends GenRecordType(EdlNamespaceName \ "PrimitiveDataType", DataTypeType, Nil)


  object StringDataTypeType extends GenRecordType(
    EdlNamespaceName \ "StringDataType", Seq(PrimitiveDataTypeType), Nil
  )


  val CircularFooType: RecordType = new RecordType(EdlNamespaceName \ "Foo", Nil, Seq(FooFields.Foo, FooFields.Bar))


  object FooFields {

    val Foo = Field("foo", CircularFooType)

    val Bar = Field("bar", CircularBarType)

  }

  val CircularBarType: RecordType = new RecordType(EdlNamespaceName \ "Bar", Nil, Seq(BarFields.Foo, BarFields.Bar))


  object BarFields {

    val Foo = Field("foo", CircularFooType)

    val Bar = Field("bar", CircularBarType)

  }


}


trait RawEdlEdl extends RawNames with RawTypes with RawBuiltins with RawEdlNamesEdl with RawEdlTypesEdl


object RawEdlTest extends RawEdlEdl {

  def main(args: Array[String]) {
    println(EdlNamespaceName)
    println(StringDataTypeType)
    println(CircularBarType)
    println(CircularFooType)
  }

}
