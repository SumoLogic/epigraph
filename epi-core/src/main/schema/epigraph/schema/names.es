namespace epigraph.schema

import epigraph

string Name

string LocalName extends Name

string QualifiedName extends Name

string LocalNamespaceName extends LocalName

string QualifiedNamespaceName extends QualifiedName

string LocalTypeName extends LocalName

string LocalTypeName extends LocalName

string QualifiedTypeName extends QualifiedName

string TypeMemberName extends LocalName

string FieldName extends LocalName

string TagName extends LocalName

string EnumMemberName extends LocalName
