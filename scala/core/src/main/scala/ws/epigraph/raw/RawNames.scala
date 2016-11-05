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

/* Created by yegor on 4/27/16. */

package ws.epigraph.raw

import ws.epigraph.gen.{GenNames, NamingConvention}

import scala.language.implicitConversions

trait RawNames extends GenNames {

  override type Name = NameApi // TODO remove such type members? probably not - these allow to provide enhanced common features

  override type QualifiedName = QualifiedNameApi


  abstract class LocalName(override val string: String, convention: NamingConvention) extends Name with LocalNameApi {

    if (!convention.isValidName(string)) throw new IllegalArgumentException(string)

  }


  protected abstract class QualifiedNameBase[+LN >: Null <: LocalName](
      override val namespace: Option[QualifiedNamespaceName],
      override val local: LN
  ) extends Name with QualifiedNameApi {

    override val string: String = namespace match {
      case Some(nn) => nn.string + '.' + local.string
      case None => local.string
    }

  }


  class LocalNamespaceName(string: String) extends LocalName(
    string, NamingConvention.LowerCamelCase
  ) with LocalNamespaceNameApi


  object LocalNamespaceName {

    implicit def apply(string: String): LocalNamespaceName = new LocalNamespaceName(string)

  }


  class QualifiedNamespaceName(
      namespace: Option[QualifiedNamespaceName],
      local: LocalNamespaceName
  ) extends QualifiedNameBase[LocalNamespaceName](namespace, local) with QualifiedNamespaceNameApi {

    def \(local: LocalTypeName): QualifiedTypeName = QualifiedTypeName(this, local)

    def /(local: LocalNamespaceName): QualifiedNamespaceName = QualifiedNamespaceName(this, local)

  }


  object QualifiedNamespaceName {

    implicit def apply(namespace: Option[QualifiedNamespaceName], local: LocalNamespaceName): QualifiedNamespaceName =
      new QualifiedNamespaceName(namespace, local)

    implicit def apply(namespace: QualifiedNamespaceName, local: LocalNamespaceName): QualifiedNamespaceName =
      new QualifiedNamespaceName(Some(namespace), local)

  }


  class LocalTypeName(string: String) extends LocalName(string, NamingConvention.UpperCamelCase) with LocalTypeNameApi


  object LocalTypeName {

    implicit def apply(string: String): LocalTypeName = new LocalTypeName(string)

  }


  class QualifiedTypeName(
      namespace: Option[QualifiedNamespaceName],
      local: LocalTypeName
  ) extends QualifiedNameBase[LocalTypeName](namespace, local) with QualifiedTypeNameApi {

    /**
     * @return [[QualifiedTypeName]] of list type with this element type name
     */
    override def listOf: QualifiedTypeName = ??? // TODO "listOf.foo.bar.Baz"? "list[foo.bar.Baz]"? special type for anonymous lists?

  }


  object QualifiedTypeName {

    implicit def apply(namespace: Option[QualifiedNamespaceName], local: LocalTypeName): QualifiedTypeName =
      new QualifiedTypeName(namespace, local)

    implicit def apply(namespace: QualifiedNamespaceName, local: LocalTypeName): QualifiedTypeName =
      new QualifiedTypeName(Some(namespace), local)

    implicit def apply(namespace: Option[QualifiedNamespaceName], local: String): QualifiedTypeName =
      new QualifiedTypeName(namespace, LocalTypeName(local))

    implicit def apply(namespace: QualifiedNamespaceName, local: String): QualifiedTypeName =
      new QualifiedTypeName(Some(namespace), LocalTypeName(local))

  }


  class TypeMemberName(string: String) extends LocalName(
    string, NamingConvention.LowerCamelCase
  ) with TypeMemberNameApi


  object TypeMemberName extends TypeMemberNameStaticApi {

    override val default: TypeMemberName = new TypeMemberName("default")

    implicit def apply(string: String): TypeMemberName = new TypeMemberName(string)

  }


  class FieldName(string: String) extends LocalName(string, NamingConvention.LowerCamelCase) with FieldNameApi


  object FieldName {

    implicit def apply(string: String): FieldName = new FieldName(string)

  }


  object TagName {

    implicit def apply(string: String): TagName = new TagName(string)

  }


  class TagName(string: String) extends LocalName(string, NamingConvention.LowerCamelCase) with TagNameApi


  class EnumValueName(string: String) extends LocalName(string, NamingConvention.LowerCamelCase) with EnumValueNameApi


}
