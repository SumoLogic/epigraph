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

/* Created by yegor on 4/26/16. */

package ws.epigraph.gen

trait GenNames { // TODO use RawNames implementation and drop member types all together?

  type Name >: Null <: AnyRef with NameApi

  type LocalName >: Null <: Name with LocalNameApi

  type QualifiedName >: Null <: Name with QualifiedNameApi

  type LocalNamespaceName >: Null <: LocalName with LocalNamespaceNameApi

  type QualifiedNamespaceName >: Null <: QualifiedName with QualifiedNamespaceNameApi

  type LocalTypeName >: Null <: LocalName with LocalTypeNameApi

  type QualifiedTypeName >: Null <: QualifiedName with QualifiedTypeNameApi

  type TypeMemberName >: Null <: LocalName with TypeMemberNameApi // TODO rename to VarMemberName (along with TypeMember)??

  val TypeMemberName: TypeMemberNameStaticApi

  type FieldName >: Null <: LocalName with FieldNameApi

  type TagName >: Null <: LocalName with TagNameApi

  type EnumValueName >: Null <: LocalName with EnumValueNameApi


  trait Named[N <: Name] {

    def name: N

  }


  trait NameApi {this: Name =>

    def string: String // TODO asString? string?

  }


  trait LocalNameApi extends NameApi {this: LocalName =>}


  trait QualifiedNameApi extends NameApi {this: QualifiedName =>

    def namespace: Option[QualifiedNamespaceName]

    def local: LocalName

  }


  trait LocalNamespaceNameApi extends LocalNameApi {this: LocalNamespaceName =>}


  trait QualifiedNamespaceNameApi extends QualifiedNameApi {this: QualifiedNamespaceName =>

    override def local: LocalNamespaceName

  }


  trait QualifiedTypeNameApi extends QualifiedNameApi {this: QualifiedTypeName =>

    override def local: LocalTypeName

    /**
     * @return [[QualifiedTypeName]] of list type with this element type name
     */
    def listOf: QualifiedTypeName

  }


  trait LocalTypeNameApi extends LocalNameApi {this: LocalTypeName =>}


  trait TypeMemberNameApi extends LocalNameApi {this: TypeMemberName =>}


  trait TypeMemberNameStaticApi {

    def default: TypeMemberName

  }


  trait FieldNameApi extends LocalNameApi {this: FieldName =>}


  trait TagNameApi extends LocalNameApi {this: TagName =>}


  trait EnumValueNameApi extends LocalNameApi {this: EnumValueName =>}


}
