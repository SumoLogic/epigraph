namespace epigraph.schema

abstract string Name

abstract string LocalName extends Name

abstract string QualifiedName extends Name

abstract record QualifiedNameStruct {

  namespace: QualifiedNamespaceNameVar

  abstract local: LocalName

}

/*abstract */vartype QualifiedNameVar default `string` {

  /*abstract*/ string: QualifiedName

  /*abstract*/ struct: QualifiedNameStruct

}

string LocalNamespaceName extends LocalName

string QualifiedNamespaceName extends QualifiedName

record QualifiedNamespaceNameStruct extends QualifiedNameStruct {

  override local: LocalNamespaceName

}

vartype QualifiedNamespaceNameVar extends QualifiedNameVar {

  override string: QualifiedNamespaceName

  override struct: QualifiedNamespaceNameStruct

}

string LocalTypeName extends LocalName

string QualifiedTypeName extends QualifiedName

record QualifiedTypeNameStruct extends QualifiedNameStruct {

  override local: LocalTypeName

}

vartype QualifiedTypeNameVar extends QualifiedNameVar {

  override string: QualifiedTypeName

  override struct: QualifiedTypeNameStruct

}

string TypeMemberName extends LocalName

string FieldName extends LocalName

string EnumValueName extends LocalName

abstract record Named {

  abstract name: Name

}
