namespace epigraph.schema

import epigraph.*

abstract string Name

string LocalName extends Name

abstract string QualifiedName extends Name // TODO vartype with string and structured representations?

string LocalNamespaceName extends LocalName

string QualifiedNamespaceName extends QualifiedName

string LocalTypeName extends LocalName

string QualifiedTypeName extends QualifiedName

string TypeMemberName extends LocalName

string FieldName extends LocalName

string EnumValueName extends LocalName

abstract record Named {
  name: Name
}
