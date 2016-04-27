/* Created by yegor on 4/26/16. */

package com.sumologic.epigraph.core

trait Names {self: Nature =>

  type Name >: Null <: AnyRef with NameApi // TODO LocalName? ShortName?

  trait NameApi {this: Name =>

    def toString: String // TODO asString? string?

  }


  type LocalName >: Null <: Name with LocalNameApi


  trait LocalNameApi extends NameApi {this: LocalName =>}


  type QualifiedName >: Null <: Name with QualifiedNameApi


  trait QualifiedNameApi extends NameApi {this: QualifiedName =>

    def namespaceName: Option[QualifiedNamespaceName]

    def localName: LocalName

  }


  type QualifiedNamespaceName >: Null <: QualifiedName with QualifiedNamespaceNameApi


  trait QualifiedNamespaceNameApi extends QualifiedNameApi {this: QualifiedNamespaceName =>

    override def localName: LocalNamespaceName

  }


  type LocalNamespaceName >: Null <: LocalName with LocalNamespaceNameApi


  trait LocalNamespaceNameApi extends LocalNameApi {this: LocalNamespaceName =>}


  type QualifiedTypeName >: Null <: QualifiedName with QualifiedTypeNameApi


  trait QualifiedTypeNameApi extends QualifiedNameApi {this: QualifiedTypeName =>

    override def localName: TypeName

  }


  type TypeName >: Null <: LocalName with TypeNameApi


  trait TypeNameApi extends LocalNameApi {this: TypeName =>}


  type TypeMemberName >: Null <: LocalName with TypeMemberNameApi // TODO Var[Value]TypeMemberName?

  trait TypeMemberNameApi extends LocalNameApi {this: TypeMemberName =>}


  type FieldName >: Null <: LocalName with FieldNameApi


  trait FieldNameApi extends LocalNameApi {this: FieldName =>}


  type TagName >: Null <: LocalName with TagNameApi


  trait TagNameApi extends LocalNameApi {this: TagName =>}


  type EnumMemberName >: Null <: LocalName with EnumMemberNameApi


  trait EnumMemberNameApi extends LocalNameApi {this: EnumMemberName =>}


}
