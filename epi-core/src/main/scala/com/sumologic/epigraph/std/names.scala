/* Created by yegor on 5/12/16. */

package com.sumologic.epigraph.std

import com.sumologic.epigraph.gen.NamingConvention

import scala.language.implicitConversions

trait Named[+N <: NameApi] {

  def name: N

}


trait NameApi {

  def string: String // TODO asString? string?

}


trait LocalNameApi extends NameApi {this: LocalName =>}


trait QualifiedNameApi extends NameApi {

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


abstract class LocalName(override val string: String, convention: NamingConvention) extends LocalNameApi {

  if (!convention.isValidName(string)) throw new IllegalArgumentException(string)

}


protected abstract class QualifiedNameBase[+LN >: Null <: LocalName](
    override val namespace: Option[QualifiedNamespaceName],
    override val local: LN
) extends QualifiedNameApi {

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


class TypeMemberName(string: String) extends LocalName(string, NamingConvention.LowerCamelCase) with TypeMemberNameApi


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

