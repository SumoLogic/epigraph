/* Created by yegor on 5/23/16. */

package epigraph.schema

import com.sumologic.epigraph.names
import com.sumologic.epigraph.xp._
import com.sumologic.epigraph.xp.data.{RecordDatum, StringDatum}
import com.sumologic.epigraph.xp.types.{Field, RecordType, StringType}

trait Name extends StringDatum[Name]


object Name extends StringType[Name](ns \ "Name")


trait LocalName extends Name with StringDatum[LocalName]


object LocalName extends StringType[LocalName](ns \ "LocalName", Seq(Name))


trait QualifiedName extends Name with StringDatum[QualifiedName]


object QualifiedName extends StringType[QualifiedName](ns \ "QualifiedName", Seq(Name))


trait LocalNamespaceName extends LocalName with StringDatum[LocalNamespaceName]


object LocalNamespaceName extends StringType[LocalNamespaceName](ns \ "LocalNamespaceName", Seq(LocalName))


trait QualifiedNamespaceName extends QualifiedName with StringDatum[QualifiedNamespaceName]


object QualifiedNamespaceName extends StringType[QualifiedNamespaceName](
  ns \ "QualifiedNamespaceName", Seq(QualifiedName)
)


trait LocalTypeName extends LocalName with StringDatum[LocalTypeName]


object LocalTypeName extends StringType[LocalTypeName](ns \ "LocalTypeName", Seq(LocalName))


trait QualifiedTypeName extends QualifiedName with StringDatum[QualifiedTypeName]


object QualifiedTypeName extends StringType[QualifiedTypeName](ns \ "QualifiedTypeName", Seq(QualifiedName))


trait TypeMemberName extends LocalName with StringDatum[TypeMemberName]


object TypeMemberName extends StringType[TypeMemberName](ns \ "TypeMemberName", Seq(LocalName))


trait FieldName extends LocalName with StringDatum[FieldName]


object FieldName extends StringType[FieldName](ns \ "FieldName", Seq(LocalName))


trait EnumValueName extends LocalName with StringDatum[EnumValueName]


object EnumValueName extends StringType[EnumValueName](ns \ "EnumValueName", Seq(LocalName))


trait Named extends RecordDatum[Named] {

//  def getName: Name = get(Named.Name, Name.default)

}


object Named extends RecordType[Named](ns \ "Named") {

  // TODO move outside of object constructor; duplicate all inherited fields?
  val _name: DatumField[Name] = field[Name]("name", Name)

  override def declaredFields: Seq[Field[Named, _]] = Seq(_name)

}
