/* Created by yegor on 5/23/16. */

package epigraph.schema

import com.sumologic.epigraph.xp.data.immutable._
import com.sumologic.epigraph.xp.data.{RecordDatum, StringDatum}
import com.sumologic.epigraph.xp.types.{Field, RecordType, StringType}

trait Name extends StringDatum[Name]


trait ImmName extends ImmStringDatum[Name] with Name


object Name extends StringType[Name](ns \ "Name") {

  override def createImmutable(native: String): ImmName = new ImmNameImpl(native)


  private class ImmNameImpl(native: String)
      extends ImmStringDatumImpl[Name](this, native) with ImmName


}


trait LocalName extends Name with StringDatum[LocalName]


trait ImmLocalName extends ImmName with LocalName with ImmStringDatum[LocalName] // TODO add ImmSupers to others

object LocalName extends StringType[LocalName](ns \ "LocalName", Seq(Name)) {

  override def createImmutable(native: String): ImmLocalName = new ImmLocalNameImpl(native)


  private class ImmLocalNameImpl(native: String)
      extends ImmStringDatumImpl[LocalName](this, native) with ImmLocalName


}


trait QualifiedName extends Name with StringDatum[QualifiedName]


trait ImmQualifiedName extends ImmStringDatum[QualifiedName] with QualifiedName


object QualifiedName extends StringType[QualifiedName](ns \ "QualifiedName", Seq(Name)) {

  override def createImmutable(native: String): ImmQualifiedName = new ImmQualifiedNameImpl(native)


  private class ImmQualifiedNameImpl(override val native: String)
      extends ImmStringDatumImpl[QualifiedName](this, native) with ImmQualifiedName


}


trait LocalNamespaceName extends LocalName with StringDatum[LocalNamespaceName]


trait ImmLocalNamespaceName extends ImmStringDatum[LocalNamespaceName] with LocalNamespaceName


object LocalNamespaceName extends StringType[LocalNamespaceName](ns \ "LocalNamespaceName", Seq(LocalName)) {

  override def createImmutable(native: String): ImmLocalNamespaceName = new ImmLocalNamespaceNameImpl(native)


  private class ImmLocalNamespaceNameImpl(override val native: String)
      extends ImmStringDatumImpl[LocalNamespaceName](this, native) with ImmLocalNamespaceName


}


trait QualifiedNamespaceName extends QualifiedName with StringDatum[QualifiedNamespaceName]


trait ImmQualifiedNamespaceName extends ImmStringDatum[QualifiedNamespaceName] with QualifiedNamespaceName


object QualifiedNamespaceName extends StringType[QualifiedNamespaceName](
  ns \ "QualifiedNamespaceName", Seq(QualifiedName)
) {

  override def createImmutable(native: String): ImmQualifiedNamespaceName = new ImmQualifiedNamespaceNameImpl(native)


  private class ImmQualifiedNamespaceNameImpl(override val native: String)
      extends ImmStringDatumImpl[QualifiedNamespaceName](this, native) with ImmQualifiedNamespaceName


}


trait LocalTypeName extends LocalName with StringDatum[LocalTypeName]


trait ImmLocalTypeName extends ImmStringDatum[LocalTypeName] with LocalTypeName


object LocalTypeName extends StringType[LocalTypeName](ns \ "LocalTypeName", Seq(LocalName)) {

  override def createImmutable(native: String): ImmLocalTypeName = new ImmLocalTypeNameImpl(native)


  private class ImmLocalTypeNameImpl(override val native: String)
      extends ImmStringDatumImpl[LocalTypeName](this, native) with ImmLocalTypeName


}


trait QualifiedTypeName extends QualifiedName with StringDatum[QualifiedTypeName]


trait ImmQualifiedTypeName extends ImmStringDatum[QualifiedTypeName] with QualifiedTypeName


object QualifiedTypeName extends StringType[QualifiedTypeName](ns \ "QualifiedTypeName", Seq(QualifiedName)) {

  override def createImmutable(native: String): ImmQualifiedTypeName = new ImmQualifiedTypeNameImpl(native)


  private class ImmQualifiedTypeNameImpl(override val native: String)
      extends ImmStringDatumImpl[QualifiedTypeName](this, native) with ImmQualifiedTypeName


}


trait TypeMemberName extends LocalName with StringDatum[TypeMemberName]


trait ImmTypeMemberName extends ImmStringDatum[TypeMemberName] with TypeMemberName


object TypeMemberName extends StringType[TypeMemberName](ns \ "TypeMemberName", Seq(LocalName)) {

  override def createImmutable(native: String): ImmTypeMemberName = new ImmTypeMemberNameImpl(native)


  private class ImmTypeMemberNameImpl(override val native: String)
      extends ImmStringDatumImpl[TypeMemberName](this, native) with ImmTypeMemberName


}


trait FieldName extends LocalName with StringDatum[FieldName]


trait ImmFieldName extends ImmStringDatum[FieldName] with FieldName


object FieldName extends StringType[FieldName](ns \ "FieldName", Seq(LocalName)) {

  override def createImmutable(native: String): ImmFieldName = new ImmFieldNameImpl(native)


  private class ImmFieldNameImpl(override val native: String)
      extends ImmStringDatumImpl[FieldName](this, native) with ImmFieldName


}


trait EnumValueName extends LocalName with StringDatum[EnumValueName]


trait ImmEnumValueName extends ImmStringDatum[EnumValueName] with EnumValueName


object EnumValueName extends StringType[EnumValueName](ns \ "EnumValueName", Seq(LocalName)) {

  override def createImmutable(native: String): ImmEnumValueName = new ImmEnumValueNameImpl(native)


  private class ImmEnumValueNameImpl(override val native: String)
      extends ImmStringDatumImpl[EnumValueName](this, native) with ImmEnumValueName


}


trait Named extends RecordDatum[Named] {

//  def getName: Name = get(Named.Name, Name.default)

}


object Named extends RecordType[Named](ns \ "Named") {

  // TODO move outside of object constructor; duplicate all inherited fields?
  val _name: DatumField[Name] = field[Name]("name", Name)

  override def declaredFields: Seq[Field[Named, _]] = Seq(_name)

}
