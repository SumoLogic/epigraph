/* Created by yegor on 4/26/16. */

package com.sumologic.epigraph.core

trait Names {self: Nature =>

  type Name >: Null <: AnyRef with NameApi // TODO LocalName?

  trait NameApi {this: Name =>

    def toString: String // TODO asString? string?

  }


  type QualifiedName >: Null <: Name with QualifiedNameApi


  trait QualifiedNameApi extends NameApi {this: QualifiedName =>

    def namespaceName: Option[Namespace]

    def localName: Name

  }


  type Namespace >: Null <: QualifiedName with NamespaceApi


  trait NamespaceApi extends QualifiedNameApi {this: Namespace =>

    def prefix: Option[Namespace]

    override def localName: NamespaceName

  }


  type NamespaceName >: Null <: Name with NamespaceNameApi


  trait NamespaceNameApi extends NameApi {this: NamespaceName =>}


  type QualifiedTypeName >: Null <: QualifiedName with QualifiedTypeNameApi


  trait QualifiedTypeNameApi extends QualifiedNameApi {this: QualifiedTypeName =>

    override def localName: TypeName

  }


  type TypeName >: Null <: Name with TypeNameApi


  trait TypeNameApi extends NameApi {this: TypeName =>}


  type TypeMemberName >: Null <: Name with TypeMemberNameApi // TODO Var[Value]TypeMemberName?

  trait TypeMemberNameApi extends NameApi {this: TypeMemberName =>}


  type FieldName >: Null <: Name with FieldNameApi


  trait FieldNameApi extends NameApi {this: FieldName =>}


  type TagName >: Null <: Name with TagNameApi


  trait TagNameApi extends NameApi {this: TagName =>}


  type EnumMemberName >: Null <: Name with EnumMemberNameApi


  trait EnumMemberNameApi extends NameApi {this: EnumMemberName =>}


}
