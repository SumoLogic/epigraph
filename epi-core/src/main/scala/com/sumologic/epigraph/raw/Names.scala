/* Created by yegor on 4/27/16. */

package com.sumologic.epigraph.raw

import com.sumologic.epigraph.core

trait Names extends core.Names {

  override type Name = NameApi // TODO remove such type members? probably not - these allow to provide enhanced common features

  override type QualifiedName = QualifiedNameApi

// TODO add NamingConvention for validation, pass it to abstract constructors

  abstract class LocalName(override val string: String) extends Name with LocalNameApi


  protected abstract class QualifiedNameBase[+LN >: Null <: LocalName](
      override val namespace: Option[QualifiedNamespaceName],
      override val local: LN
  ) extends Name with QualifiedNameApi {

    override val string: String = namespace match {
      case Some(nn) => nn.string + '.' + local.string
      case None => local.string
    }

  }


  class LocalNamespaceName(string: String) extends LocalName(string) with LocalNamespaceNameApi


  class QualifiedNamespaceName(
      namespace: Option[QualifiedNamespaceName],
      local: LocalNamespaceName
  ) extends QualifiedNameBase[LocalNamespaceName](namespace, local) with QualifiedNamespaceNameApi


  class LocalTypeName(string: String) extends LocalName(string) with LocalTypeNameApi


  class QualifiedTypeName(
      namespace: Option[QualifiedNamespaceName],
      local: LocalTypeName
  ) extends QualifiedNameBase[LocalTypeName](namespace, local) with QualifiedTypeNameApi


  class TypeMemberName(string: String) extends LocalName(string) with TypeMemberNameApi


  class FieldName(string: String) extends LocalName(string) with FieldNameApi


  class TagName(string: String) extends LocalName(string) with TagNameApi


  class EnumMemberName(string: String) extends LocalName(string) with EnumMemberNameApi


}
