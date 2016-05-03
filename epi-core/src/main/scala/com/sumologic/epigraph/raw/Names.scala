/* Created by yegor on 4/27/16. */

package com.sumologic.epigraph.raw

import com.sumologic.epigraph.core
import com.sumologic.epigraph.core.NamingConvention

import scala.language.implicitConversions

trait Names extends core.Names {

  override type Name = NameApi // TODO remove such type members? probably not - these allow to provide enhanced common features

  override type QualifiedName = QualifiedNameApi

// TODO add NamingConvention for validation, pass it to abstract constructors

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

    def apply(local: LocalTypeName): QualifiedTypeName = QualifiedTypeName(this, local)

    def apply(local: LocalNamespaceName): QualifiedNamespaceName = QualifiedNamespaceName(this, local)

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
  ) extends QualifiedNameBase[LocalTypeName](namespace, local) with QualifiedTypeNameApi


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

  }


  class FieldName(string: String) extends LocalName(string, NamingConvention.LowerCamelCase) with FieldNameApi


  object FieldName {

    implicit def apply(string: String): FieldName = new FieldName(string)

  }


  class TagName(string: String) extends LocalName(string, NamingConvention.LowerCamelCase) with TagNameApi


  class EnumMemberName(string: String) extends LocalName(
    string, NamingConvention.LowerCamelCase
  ) with EnumMemberNameApi


}
