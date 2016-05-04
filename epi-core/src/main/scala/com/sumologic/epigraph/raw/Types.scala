/* Created by yegor on 4/28/16. */

package com.sumologic.epigraph.raw

import com.sumologic.epigraph.core

//import scala.language.implicitConversions

trait Types extends core.Types {this: core.Names =>


  abstract class Type(private val tname: QualifiedTypeName) extends TypeApi {

    override def name: QualifiedTypeName = tname

  }


  class VarType(
      name: QualifiedTypeName,
      override val defaultMember: Option[TypeMember],
      override val members: Seq[TypeMember]
  ) extends Type(name) with VarTypeApi {

    // TODO parameterized method to return any supported view of this?

    override lazy val listOf: ListDataType = new ListDataType(name.listOf, Nil, this)

  }

  class TypeMember(private val mname: TypeMemberName, override val dataType: DataType) extends TypeMemberApi {

    override def name: TypeMemberName = mname

  }


  object TypeMember {

    def apply(name: TypeMemberName, dataType: DataType): TypeMember = new TypeMember(name, dataType)

  }


  abstract class DataType(name: QualifiedTypeName, override val supertypes: Seq[DataType]) extends Type(
    name
  ) with DataTypeApi {

    override lazy val varType: VarType = {
      val member = new TypeMember(TypeMemberName.default, this)
      new VarType(name, Some(member), Seq(member))
    }

  }


//  object DataType {
//    // works but idea marks as errors
//    implicit def dataTypeToSeq[DT >: Null <: DataType](dataType: DT): Seq[DT] = Seq[DT](dataType)
//
//  }


  class RecordDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[RecordDataType],
      declaredFieldsRef: => Seq[Field]
  ) extends DataType(name, supertypes) with RecordDataTypeApi {

    def this(name: QualifiedTypeName, supertype: RecordDataType, declaredFieldsRef: => Seq[Field]) = this(
      name, Seq(supertype), declaredFieldsRef
    )

    override lazy val declaredFields: Seq[Field] = declaredFieldsRef

  }


  class Field(private val fname: FieldName, valueTypeRef: => VarType) extends FieldApi {

    override def name: FieldName = fname

    override lazy val valueType: VarType = valueTypeRef
  }


  object Field {

    def apply(name: FieldName, valueType: => VarType): Field = new Field(name, valueType)

  }


  class UnionDataType(
      name: QualifiedTypeName,
      override val declaredTags: Seq[Tag]
  ) extends DataType(name, Nil) with UnionDataTypeApi


  class Tag(private val tname: TagName, valueTypeRef: => VarType) extends TagApi {

    override def name: TagName = tname

    override lazy val valueType: VarType = valueTypeRef

  }


  object Tag {

    def apply(name: TagName, valueType: => VarType): Tag = new Tag(name, valueType)

  }


  class MapDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[MapDataType],
      override val keyType: DataType,
      override val valueType: VarType
  ) extends DataType(name, supertypes) with MapDataTypeApi


  class ListDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[ListDataType],
      override val valueType: VarType
  ) extends DataType(name, supertypes) with ListDataTypeApi


  class EnumDataType(
      name: QualifiedTypeName,
      override val members: Seq[EnumTypeMember]
  ) extends DataType(name, Nil) with EnumDataTypeApi


  class EnumTypeMember(private val ename: EnumMemberName) extends EnumTypeMemberApi {

    override def name: EnumMemberName = ename

  }


  abstract class PrimitiveDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[PrimitiveDataType]
  ) extends DataType(name, supertypes) with PrimitiveDataTypeApi


  class StringDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[StringDataType]
  ) extends PrimitiveDataType(name, supertypes) with StringDataTypeApi {

    def this(name: QualifiedTypeName, supertype: StringDataType) = this(name, Seq(supertype))

  }


}
