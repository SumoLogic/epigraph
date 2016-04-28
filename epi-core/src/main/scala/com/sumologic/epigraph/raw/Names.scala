/* Created by yegor on 4/27/16. */

package com.sumologic.epigraph.raw

import com.sumologic.epigraph.core

trait Names extends core.Names {

  override type Name = NameApi // TODO remove such type members? probably not - these allow to provide enhanced common features

  override type LocalName = RawLocalName

  override type QualifiedName = RawQualifiedName[LocalName]

  override type LocalNamespaceName = RawLocalNamespaceName

  override type QualifiedNamespaceName = RawQualifiedNamespaceName

  override type LocalTypeName = RawLocalTypeName

  override type QualifiedTypeName = RawQualifiedTypeName

  override type TypeMemberName = RawTypeMemberName

  override type FieldName = RawFieldName

  override type TagName = RawTagName

  override type EnumMemberName = RawEnumMemberName

// TODO add NamingConvention for validation, pass it to abstract constructors

  protected abstract class RawLocalName(override val string: String) extends Name with LocalNameApi


  protected abstract class RawQualifiedName[+LN >: Null <: LocalName](
      override val namespaceName: Option[QualifiedNamespaceName],
      override val localName: LN
  ) extends Name with QualifiedNameApi {

    override val string: String = namespaceName match {
      case Some(nn) => nn.string + '.' + localName.string
      case None => localName.string
    }

  }


  class RawLocalNamespaceName(override val string: String) extends LocalName(string) with LocalNamespaceNameApi


  class RawQualifiedNamespaceName(
      namespaceName: Option[QualifiedNamespaceName],
      local: LocalNamespaceName
  ) extends RawQualifiedName[LocalNamespaceName](namespaceName, local) with QualifiedNamespaceNameApi


  class RawLocalTypeName(string: String) extends LocalName(string) with LocalTypeNameApi


  class RawQualifiedTypeName(
      namespaceName: Option[QualifiedNamespaceName],
      local: LocalTypeName
  ) extends RawQualifiedName[LocalTypeName](namespaceName, local) with QualifiedTypeNameApi


  class RawTypeMemberName(string: String) extends LocalName(string) with TypeMemberNameApi


  class RawFieldName(string: String) extends LocalName(string) with FieldNameApi


  class RawTagName(string: String) extends LocalName(string) with TagNameApi


  class RawEnumMemberName(string: String) extends LocalName(string) with EnumMemberNameApi


}
