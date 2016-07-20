/* Created by yegor on 5/23/16. */

package epigraph.schema

import com.sumologic.epigraph.xp.data.immutable._
import com.sumologic.epigraph.xp.data.mutable._
import com.sumologic.epigraph.xp.data.{RecordDatum, StringDatum}
import com.sumologic.epigraph.xp.types.{Field, RecordType, StringType}

trait Name extends StringDatum[Name]


trait ImmName extends ImmStringDatum[Name] with Name


trait MutName extends MutStringDatum[Name] with Name


object Name extends StringType[Name](ns \ "Name") {

  override def createImmutable(native: String): ImmName = new ImmNameImpl(native)


  private class ImmNameImpl(native: String)
      extends ImmStringDatumImpl[Name](this, native) with ImmName


  override def createMutable(native: String): MutName = new MutNameImpl(native)


  private class MutNameImpl(native: String)
      extends MutStringDatumImpl[Name](this, native) with MutName


}


trait LocalName extends Name with StringDatum[LocalName]


trait ImmLocalName extends ImmName with LocalName with ImmStringDatum[LocalName]


// TODO add ImmSupers to others
trait MutLocalName extends MutName with LocalName with MutStringDatum[LocalName] // TODO add MutSupers to others

object LocalName extends StringType[LocalName](ns \ "LocalName", Seq(Name)) {

  override def createImmutable(native: String): ImmLocalName = new ImmLocalNameImpl(native)


  private class ImmLocalNameImpl(native: String)
      extends ImmStringDatumImpl[LocalName](this, native) with ImmLocalName


  override def createMutable(native: String): MutLocalName = new MutLocalNameImpl(native)


  private class MutLocalNameImpl(native: String)
      extends MutStringDatumImpl[LocalName](this, native) with MutLocalName


}


trait QualifiedName extends Name with StringDatum[QualifiedName]


trait ImmQualifiedName extends ImmName with QualifiedName with ImmStringDatum[QualifiedName]


trait MutQualifiedName extends MutName with QualifiedName with MutStringDatum[QualifiedName]


object QualifiedName extends StringType[QualifiedName](ns \ "QualifiedName", Seq(Name)) {

  override def createImmutable(native: String): ImmQualifiedName = new ImmQualifiedNameImpl(native)


  private class ImmQualifiedNameImpl(native: String)
      extends ImmStringDatumImpl[QualifiedName](this, native) with ImmQualifiedName


  override def createMutable(native: String): MutQualifiedName = new MutQualifiedNameImpl(native)


  private class MutQualifiedNameImpl(native: String)
      extends MutStringDatumImpl[QualifiedName](this, native) with MutQualifiedName


}


trait LocalNamespaceName extends LocalName with StringDatum[LocalNamespaceName]


trait ImmLocalNamespaceName extends ImmLocalName with LocalNamespaceName with ImmStringDatum[LocalNamespaceName]


trait MutLocalNamespaceName extends MutLocalName with LocalNamespaceName with MutStringDatum[LocalNamespaceName]


object LocalNamespaceName extends StringType[LocalNamespaceName](ns \ "LocalNamespaceName", Seq(LocalName)) {

  override def createImmutable(native: String): ImmLocalNamespaceName = new ImmLocalNamespaceNameImpl(native)


  private class ImmLocalNamespaceNameImpl(native: String)
      extends ImmStringDatumImpl[LocalNamespaceName](this, native) with ImmLocalNamespaceName


  override def createMutable(native: String): MutLocalNamespaceName = new MutLocalNamespaceNameImpl(native)


  private class MutLocalNamespaceNameImpl(native: String)
      extends MutStringDatumImpl[LocalNamespaceName](this, native) with MutLocalNamespaceName


}


trait QualifiedNamespaceName extends QualifiedName with StringDatum[QualifiedNamespaceName]


trait ImmQualifiedNamespaceName extends ImmQualifiedName with QualifiedNamespaceName with ImmStringDatum[QualifiedNamespaceName]


trait MutQualifiedNamespaceName extends MutQualifiedName with QualifiedNamespaceName with MutStringDatum[QualifiedNamespaceName]


object QualifiedNamespaceName extends StringType[QualifiedNamespaceName](
  ns \ "QualifiedNamespaceName", Seq(QualifiedName)
) {

  override def createImmutable(native: String): ImmQualifiedNamespaceName = new ImmQualifiedNamespaceNameImpl(native)


  private class ImmQualifiedNamespaceNameImpl(native: String)
      extends ImmStringDatumImpl[QualifiedNamespaceName](this, native) with ImmQualifiedNamespaceName


  override def createMutable(native: String): MutQualifiedNamespaceName = new MutQualifiedNamespaceNameImpl(native)


  private class MutQualifiedNamespaceNameImpl(native: String)
      extends MutStringDatumImpl[QualifiedNamespaceName](this, native) with MutQualifiedNamespaceName


}


trait LocalTypeName extends LocalName with StringDatum[LocalTypeName]


trait ImmLocalTypeName extends ImmLocalName with LocalTypeName with ImmStringDatum[LocalTypeName]


trait MutLocalTypeName extends MutLocalName with LocalTypeName with MutStringDatum[LocalTypeName]


object LocalTypeName extends StringType[LocalTypeName](ns \ "LocalTypeName", Seq(LocalName)) {

  override def createImmutable(native: String): ImmLocalTypeName = new ImmLocalTypeNameImpl(native)


  private class ImmLocalTypeNameImpl(native: String)
      extends ImmStringDatumImpl[LocalTypeName](this, native) with ImmLocalTypeName


  override def createMutable(native: String): MutLocalTypeName = new MutLocalTypeNameImpl(native)


  private class MutLocalTypeNameImpl(native: String)
      extends MutStringDatumImpl[LocalTypeName](this, native) with MutLocalTypeName


}


trait QualifiedTypeName extends QualifiedName with StringDatum[QualifiedTypeName]


trait ImmQualifiedTypeName extends ImmQualifiedName with QualifiedTypeName with ImmStringDatum[QualifiedTypeName]


trait MutQualifiedTypeName extends MutQualifiedName with QualifiedTypeName with MutStringDatum[QualifiedTypeName]


object QualifiedTypeName extends StringType[QualifiedTypeName](ns \ "QualifiedTypeName", Seq(QualifiedName)) {

  override def createImmutable(native: String): ImmQualifiedTypeName = new ImmQualifiedTypeNameImpl(native)


  private class ImmQualifiedTypeNameImpl(native: String)
      extends ImmStringDatumImpl[QualifiedTypeName](this, native) with ImmQualifiedTypeName


  override def createMutable(native: String): MutQualifiedTypeName = new MutQualifiedTypeNameImpl(native)


  private class MutQualifiedTypeNameImpl(native: String)
      extends MutStringDatumImpl[QualifiedTypeName](this, native) with MutQualifiedTypeName


}


trait TypeMemberName extends LocalName with StringDatum[TypeMemberName]


trait ImmTypeMemberName extends ImmLocalName with TypeMemberName with ImmStringDatum[TypeMemberName]


trait MutTypeMemberName extends MutLocalName with TypeMemberName with MutStringDatum[TypeMemberName]


object TypeMemberName extends StringType[TypeMemberName](ns \ "TypeMemberName", Seq(LocalName)) {

  override def createImmutable(native: String): ImmTypeMemberName = new ImmTypeMemberNameImpl(native)


  private class ImmTypeMemberNameImpl(native: String)
      extends ImmStringDatumImpl[TypeMemberName](this, native) with ImmTypeMemberName


  override def createMutable(native: String): MutTypeMemberName = new MutTypeMemberNameImpl(native)


  private class MutTypeMemberNameImpl(native: String)
      extends MutStringDatumImpl[TypeMemberName](this, native) with MutTypeMemberName


}


trait FieldName extends LocalName with StringDatum[FieldName]


trait ImmFieldName extends ImmLocalName with ImmStringDatum[FieldName] with FieldName


trait MutFieldName extends MutLocalName with MutStringDatum[FieldName] with FieldName


object FieldName extends StringType[FieldName](ns \ "FieldName", Seq(LocalName)) {

  override def createImmutable(native: String): ImmFieldName = new ImmFieldNameImpl(native)


  private class ImmFieldNameImpl(native: String)
      extends ImmStringDatumImpl[FieldName](this, native) with ImmFieldName


  override def createMutable(native: String): MutFieldName = new MutFieldNameImpl(native)


  private class MutFieldNameImpl(native: String)
      extends MutStringDatumImpl[FieldName](this, native) with MutFieldName


}


trait EnumValueName extends LocalName with StringDatum[EnumValueName]


trait ImmEnumValueName extends ImmLocalName with EnumValueName with ImmStringDatum[EnumValueName]


trait MutEnumValueName extends MutLocalName with EnumValueName with MutStringDatum[EnumValueName]


object EnumValueName extends StringType[EnumValueName](ns \ "EnumValueName", Seq(LocalName)) {

  override def createImmutable(native: String): ImmEnumValueName = new ImmEnumValueNameImpl(native)


  private class ImmEnumValueNameImpl(native: String)
      extends ImmStringDatumImpl[EnumValueName](this, native) with ImmEnumValueName


  override def createMutable(native: String): MutEnumValueName = new MutEnumValueNameImpl(native)


  private class MutEnumValueNameImpl(native: String)
      extends MutStringDatumImpl[EnumValueName](this, native) with MutEnumValueName


}


trait Named extends RecordDatum[Named] {

//  def getName: Name = get(Named.Name, Name.default)

}


object Named extends RecordType[Named](ns \ "Named") {

  // TODO move outside of object constructor; duplicate all inherited fields?
  val _name: DatumField[Name] = field[Name]("name", Name)

  override def declaredFields: Seq[Field[Named, _]] = Seq(_name)

}
