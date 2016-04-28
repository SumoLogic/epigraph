/* Created by yegor on 4/26/16. */

package com.sumologic.epigraph.core

trait Names { // TODO use raw.Names implementation and drop member types all together?

  type Name >: Null <: AnyRef with NameApi


  trait NameApi {this: Name =>

    def string: String // TODO asString? string?

  }


  trait Named {

    def name: Name

  }


  type LocalName >: Null <: Name with LocalNameApi


  trait LocalNameApi extends NameApi {this: LocalName =>}


  type QualifiedName >: Null <: Name with QualifiedNameApi


  trait QualifiedNameApi extends NameApi {this: QualifiedName =>

    def namespaceName: Option[QualifiedNamespaceName]

    def localName: LocalName

  }


  type LocalNamespaceName >: Null <: LocalName with LocalNamespaceNameApi


  trait LocalNamespaceNameApi extends LocalNameApi {this: LocalNamespaceName =>}


  type QualifiedNamespaceName >: Null <: QualifiedName with QualifiedNamespaceNameApi


  trait QualifiedNamespaceNameApi extends QualifiedNameApi {this: QualifiedNamespaceName =>

    override def localName: LocalNamespaceName

  }


  type QualifiedTypeName >: Null <: QualifiedName with QualifiedTypeNameApi


  trait QualifiedTypeNameApi extends QualifiedNameApi {this: QualifiedTypeName =>

    override def localName: LocalTypeName

  }


  type LocalTypeName >: Null <: LocalName with LocalTypeNameApi


  trait LocalTypeNameApi extends LocalNameApi {this: LocalTypeName =>}


  type TypeMemberName >: Null <: LocalName with TypeMemberNameApi // TODO rename to Var[Value]TypeMemberName??

  trait TypeMemberNameApi extends LocalNameApi {this: TypeMemberName =>}


  type FieldName >: Null <: LocalName with FieldNameApi


  trait FieldNameApi extends LocalNameApi {this: FieldName =>}


  type TagName >: Null <: LocalName with TagNameApi


  trait TagNameApi extends LocalNameApi {this: TagName =>}


  type EnumMemberName >: Null <: LocalName with EnumMemberNameApi


  trait EnumMemberNameApi extends LocalNameApi {this: EnumMemberName =>}


}
