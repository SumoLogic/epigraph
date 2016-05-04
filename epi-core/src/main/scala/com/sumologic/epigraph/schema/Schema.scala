/* Created by yegor on 5/2/16. */

package com.sumologic.epigraph.schema

import com.sumologic.epigraph.raw

trait Builtin {this: raw.Types with raw.Names => // TODO move to separate file (package?)

  val BuiltinNamespaceName = new QualifiedNamespaceName(None, new LocalNamespaceName("epigraph"))

  val StringType = new StringDataType(BuiltinNamespaceName \ "String", Nil)

}


trait SchemaNamesSchema {this: raw.Types with raw.Names with Builtin =>

  val SchemaNamespaceName: QualifiedNamespaceName = BuiltinNamespaceName / "schema"


  object NameType extends StringDataType(SchemaNamespaceName \ "Name", StringType)


  object LocalNameType extends StringDataType(SchemaNamespaceName \ "LocalName", NameType)


  object QualifiedNameType extends StringDataType(SchemaNamespaceName \ "QualifiedName", NameType)


  object LocalNamespaceNameType extends StringDataType(SchemaNamespaceName \ "LocalNamespaceName", LocalNameType)


  object QualifiedNamespaceNameType extends StringDataType(
    SchemaNamespaceName \ "QualifiedNamespaceName", QualifiedNameType
  )


  object LocalTypeNameType extends StringDataType(SchemaNamespaceName \ "LocalTypeName", LocalNameType)


  object QualifiedTypeNameType extends StringDataType(SchemaNamespaceName \ "QualifiedTypeName", QualifiedNameType)


  object TypeMemberNameType extends StringDataType(SchemaNamespaceName \ "TypeMemberName", LocalNameType)


  object FieldNameType extends StringDataType(SchemaNamespaceName \ "FieldName", LocalNameType)


  object TagNameType extends StringDataType(SchemaNamespaceName \ "TagName", LocalNameType)


  object EnumMemberNameType extends StringDataType(SchemaNamespaceName \ "EnumMemberName", LocalNameType)


}


trait SchemaTypesSchema {this: raw.Types with raw.Names with Builtin with SchemaNamesSchema =>


  object TypeType extends {

    val Name = Field("name", QualifiedTypeNameType.varType)

  } with RecordDataType(SchemaNamespaceName \ "Type", Nil, Seq(Name))


  object VarTypeType extends {

    val Default = Field("default", TypeMemberNameType.varType) // TODO ref? +ref?

    val Members = Field("members", TypeMemberType.varType.listOf.varType)

  } with RecordDataType(SchemaNamespaceName \ "VarType", TypeType, Seq(Default, Members))


  object TypeMemberType extends {

    val Name = Field("name", TypeMemberNameType.varType)

    val DataType = Field("dataType", DataTypeRefType)

  } with RecordDataType(SchemaNamespaceName \ "TypeMember", Nil, Seq(Name, DataType))


  object VarTypeRefType extends VarType(
    SchemaNamespaceName \ "VarTypeRef",
    Some(VarTypeRefMembers.Name),
    Seq(VarTypeRefMembers.Name, VarTypeRefMembers.Type)
  )


  object VarTypeRefMembers {

    val Name = TypeMember("name", QualifiedTypeNameType)

    val Type = TypeMember("type", VarTypeType)

  }


  object DataTypeRefType extends VarType(
    SchemaNamespaceName \ "DataTypeRef",
    Some(DataTypeRefMembers.Name),
    Seq(DataTypeRefMembers.Name, DataTypeRefMembers.Type)
  )


  object DataTypeRefMembers {

    val Name = TypeMember("name", QualifiedTypeNameType)

    val Type = TypeMember("type", DataTypeUnionType)

  }


  object DataTypeUnionType extends {

    val Record = Tag("record", RecordDataTypeType.varType)

    val Union = Tag("union", UnionDataTypeType.varType)

    val Map = Tag("map", MapDataTypeType.varType)

    val List = Tag("list", ListDataTypeType.varType)

    val Enum = Tag("enum", EnumDataTypeType.varType)

    val String = Tag("string", StringDataTypeType.varType)

    // TODO other primitive types

  } with UnionDataType(SchemaNamespaceName \ "DataType", Seq(Record, Union, Map, List, Enum, String))


  object DataTypeType extends {

    val Supertypes = Field("supertypes", DataTypeRefType.listOf.varType)

  } with RecordDataType(SchemaNamespaceName \ "DataType", TypeType, Seq(Supertypes))


  object RecordDataTypeType extends {

    val DeclaredFields = Field("declaredFields", FieldType.varType.listOf.varType)

  } with RecordDataType(SchemaNamespaceName \ "RecordDataType", DataTypeType, Seq(DeclaredFields))


  object FieldType extends {

    val Name = Field("name", FieldNameType.varType)

    val ValueType = Field("valueType", VarTypeRefType)

  } with RecordDataType(SchemaNamespaceName \ "Field", Nil, Seq(Name, ValueType))


  object UnionDataTypeType extends {

    val Tags = Field("tags", TagType.varType.listOf.varType)

  } with RecordDataType(SchemaNamespaceName \ "UnionDataType", DataTypeType, Seq(Tags))


  object TagType extends {

    val Name = Field("name", TagNameType.varType)

    val ValueType = Field("valueType", VarTypeRefType)

  } with RecordDataType(SchemaNamespaceName \ "Tag", Nil, Seq(Name, ValueType))


  object MapDataTypeType extends {

    val KeyType = Field("keyType", DataTypeRefType)

    val ValueType = Field("valueType", VarTypeRefType)

  } with RecordDataType(SchemaNamespaceName \ "MapDataType", DataTypeType, Seq(KeyType, ValueType))


  object ListDataTypeType extends {

    val ValueType = Field("valueType", VarTypeRefType)

  } with RecordDataType(SchemaNamespaceName \ "ListDataType", DataTypeType, Seq(ValueType))


  object EnumDataTypeType extends {

    val Members = Field("members", EnumTypeMemberType.varType)

  } with RecordDataType(SchemaNamespaceName \ "EnumDataType", DataTypeType, Seq(Members))


  object EnumTypeMemberType extends {

    val Name = Field("name", EnumMemberNameType.varType)

  } with RecordDataType(SchemaNamespaceName \ "EnumTypeMember", Nil, Seq(Name))


  object PrimitiveDataTypeType extends RecordDataType(SchemaNamespaceName \ "PrimitiveDataType", DataTypeType, Nil)


  object StringDataTypeType extends RecordDataType(
    SchemaNamespaceName \ "StringDataType", Seq(PrimitiveDataTypeType), Nil
  )


//  val CircularFooType: RecordDataType = new RecordDataType(SchemaNamespaceName \ "Foo", Nil, Seq(FooFields.Foo, FooFields.Bar))
//
//
//  object FooFields {
//
//    val Foo = Field("foo", CircularFooType.varType)
//
//    val Bar = Field("bar", CircularBarType.varType)
//
//  }
//
//  val CircularBarType: RecordDataType = new RecordDataType(SchemaNamespaceName \ "Bar", Nil, Seq(BarFields.Foo, BarFields.Bar))
//
//
//  object BarFields {
//
//    val Foo = Field("foo", CircularFooType.varType)
//
//    val Bar = Field("bar", CircularBarType.varType)
//
//  }


}


trait SchemaSchema extends raw.Names with raw.Types with Builtin with SchemaNamesSchema with SchemaTypesSchema


object Main extends SchemaSchema {

  def main(args: Array[String]) {
    println(SchemaNamespaceName)
    println(StringDataTypeType)
//    println(CircularBarType)
//    println(CircularFooType)
  }

}
