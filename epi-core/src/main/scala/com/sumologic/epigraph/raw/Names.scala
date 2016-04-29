/* Created by yegor on 4/27/16. */

package com.sumologic.epigraph.raw

import com.sumologic.epigraph.core

trait Names extends core.Names {

  override type Name = NameApi // TODO remove such type members? probably not - these allow to provide enhanced common features

//  override type LocalName = this.RawLocalName // TODO replace member types with same-named classes?

  override type QualifiedName = this.RawQualifiedName[LocalName]

//  override type LocalNamespaceName = this.RawLocalNamespaceName
//
//  override type QualifiedNamespaceName = this.RawQualifiedNamespaceName
//
//  override type LocalTypeName = this.RawLocalTypeName
//
//  override type QualifiedTypeName = this.RawQualifiedTypeName
//
//  override type TypeMemberName = this.RawTypeMemberName
//
//  override type FieldName = this.RawFieldName
//
//  override type TagName = this.RawTagName
//
//  override type EnumMemberName = this.RawEnumMemberName

// TODO add NamingConvention for validation, pass it to abstract constructors

  abstract class LocalName(override val string: String) extends Name with LocalNameApi


  protected abstract class RawQualifiedName[+LN >: Null <: LocalName](
      override val namespaceName: Option[QualifiedNamespaceName],
      override val localName: LN
  ) extends Name with QualifiedNameApi {

    override val string: String = namespaceName match {
      case Some(nn) => nn.string + '.' + localName.string
      case None => localName.string
    }

  }


  class LocalNamespaceName(override val string: String) extends LocalName(string) with LocalNamespaceNameApi


  class QualifiedNamespaceName(
      namespaceName: Option[QualifiedNamespaceName],
      local: LocalNamespaceName
  ) extends RawQualifiedName[LocalNamespaceName](namespaceName, local) with QualifiedNamespaceNameApi


  class LocalTypeName(string: String) extends LocalName(string) with LocalTypeNameApi


  class QualifiedTypeName(
      namespaceName: Option[QualifiedNamespaceName],
      local: LocalTypeName
  ) extends RawQualifiedName[LocalTypeName](namespaceName, local) with QualifiedTypeNameApi


  class TypeMemberName(string: String) extends LocalName(string) with TypeMemberNameApi


  class FieldName(string: String) extends LocalName(string) with FieldNameApi


  class TagName(string: String) extends LocalName(string) with TagNameApi


  class EnumMemberName(string: String) extends LocalName(string) with EnumMemberNameApi


}
