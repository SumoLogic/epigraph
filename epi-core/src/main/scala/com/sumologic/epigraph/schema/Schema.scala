/* Created by yegor on 5/2/16. */

package com.sumologic.epigraph.schema

import com.sumologic.epigraph.raw

trait Builtin {this: raw.Types with raw.Names => // TODO move to separate file (package?)

  val BuiltinNamespaceName = new QualifiedNamespaceName(None, new LocalNamespaceName("epigraph"))

  val StringType = new StringDataType(BuiltinNamespaceName("String"), Seq())

}


trait NamesSchema {this: raw.Types with raw.Names with Builtin =>

  val SchemaNamespaceName: QualifiedNamespaceName = BuiltinNamespaceName("schema")


  object NameType extends StringDataType(SchemaNamespaceName("Name"), StringType)


  object LocalNameType extends StringDataType(SchemaNamespaceName("LocalName"), NameType)


  object QualifiedNameType extends StringDataType(SchemaNamespaceName("QualifiedName"), NameType)


  object LocalNamespaceNameType extends StringDataType(SchemaNamespaceName("LocalNamespaceName"), LocalNameType)


  object QualifiedNamespaceNameType extends StringDataType(
    SchemaNamespaceName("QualifiedNamespaceName"), QualifiedNameType
  )


  object LocalTypeNameType extends StringDataType(SchemaNamespaceName("LocalTypeName"), LocalNameType)


  object QualifiedTypeNameType extends StringDataType(SchemaNamespaceName("QualifiedTypeName"), QualifiedNameType)


  object TypeMemberNameType extends StringDataType(SchemaNamespaceName("TypeMemberName"), LocalNameType)


  object FieldNameType extends StringDataType(SchemaNamespaceName("FieldName"), LocalNameType)


  object TagNameType extends StringDataType(SchemaNamespaceName("TagName"), LocalNameType)


  object EnumMemberNameType extends StringDataType(SchemaNamespaceName("EnumMemberName"), LocalNameType)


}


trait TypesSchema {this: raw.Types with raw.Names with Builtin with NamesSchema =>


  object TypeType extends {

    val NameField = new Field(new FieldName("name"), FieldNameType)

  } with RecordDataType(SchemaNamespaceName("Type"), Seq(), Seq(NameField))


  object VarTypeType extends {

    val DefaultField = Field("default", TypeMemberNameType)

    val MembersField = Field("members", TypeMemberNameType) // FIXME list[TypeMember]

  } with RecordDataType(SchemaNamespaceName("VarType"), TypeType, Seq(DefaultField, MembersField))


}


trait Schema extends NamesSchema with TypesSchema with raw.Types with raw.Names with Builtin {

}
