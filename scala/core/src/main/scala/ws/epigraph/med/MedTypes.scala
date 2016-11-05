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

/* Created by yegor on 5/6/16. */

package ws.epigraph.med

import ws.epigraph.gen.{GenNames, GenTypes}

trait MedTypes extends GenTypes {this: GenNames with MedData =>

  override type GenType = Type

  override type GenVarType = VarType[_]

  override type GenTypeMember = TypeMember[_]

  override type GenDataType = DataType[_]

  override type GenRecordType = RecordType[_]

  override type GenField = Field[_, _]

//  override type GenUnionType = UnionType
//
//  override type GenTag = Tag
//
//  override type GenMapType = MapType

  override type GenListType = ListType[_, _]

//  override type GenEnumType = EnumType
//
//  override type GenEnumTypeMember = EnumTypeMember

  override type GenPrimitiveType = PrimitiveType[_]

  override type GenStringType = StringType[_]


  trait Type extends TypeApi // it's a trait so static data types can mix it in and serve as own default VarType

  trait VarType[V <: Var[V]] extends Type with VarTypeApi {

    override def supertypes: Seq[VarType[_ >: V]]

    override lazy val listOf: DynamicListType[V] = new DynamicListType[V](name.listOf, supertypes.map(_.listOf), this)

  }


  class TypeMember[D <: Datum[D]](
      override val name: TypeMemberName,
      override val dataType: DataType[D]
  ) extends TypeMemberApi


  abstract class StaticVarType[V <: Var[V]](
      override val name: QualifiedTypeName,
      override val supertypes: Seq[VarType[_ >: V]]
  ) extends VarType[V]


  trait VarTypeWithDefault[V <: Var[V], D <: Datum[D]] extends VarType[V] {

    def default: TypeMember[D]

    override lazy val defaultMember: Option[TypeMember[D]] = Some(default)

  }


  trait DefaultVarType[D <: Datum[D]] extends VarTypeWithDefault[D, D] {this: DataType[D] =>

    override val default: TypeMember[D] = new TypeMember(TypeMemberName.default, this)

    override val members: Seq[TypeMember[D]] = Seq(default)

  }


  abstract class DataType[D <: Datum[D]](
      override val name: QualifiedTypeName,
      override val supertypes: Seq[DataType[_ >: D]]
  ) extends Type with DataTypeApi with DefaultVarType[D] {

  }


  abstract class RecordType[D <: RecordDatum[D]](
      override val name: QualifiedTypeName,
      override val supertypes: Seq[RecordType[_ >: D]]
  ) extends DataType[D](name, supertypes) with RecordDataTypeApi {

    val declaredFields: Seq[Field[D, _]]

  }


  abstract class Field[RD <: RecordDatum[RD], V <: Var[V]](
      override val name: FieldName,
      override val varType: VarType[V]
  ) extends FieldApi


  class DynamicRecordType(
      override val name: QualifiedTypeName,
      override val supertypes: Seq[RecordType[_ >: Null]],
      override val declaredFields: Seq[DynamicField] // TODO use def?
  ) extends RecordType[Null](name, supertypes)


  class DynamicField(override val name: FieldName, override val varType: VarType[Null]) extends Field[Null, Null](
    name, varType
  )


  abstract class StaticRecordType[D >: Null <: RecordDatum[D]](
      override val name: QualifiedTypeName,
      override val supertypes: Seq[RecordType[_ >: D]]
  ) extends RecordType[D](name, supertypes) {

    protected def field[V <: Var[V]](name: FieldName, varType: VarType[V]): StaticField[D, V] = new StaticField[D, V](
      name,
      varType
    )

  }


  // TODO take host record type?
  class StaticField[D <: RecordDatum[D], V <: Var[V]](
      override val name: FieldName,
      override val varType: VarType[V]
  ) extends Field[D, V](name, varType)


  abstract class ListType[D <: ListDatum[D, V], V <: Var[V]](
      override val name: QualifiedTypeName,
      override val supertypes: Seq[ListType[_ >: D, _ >: V]],
      override val valueVarType: VarType[V]
  ) extends DataType[D](name, supertypes) with ListDataTypeApi


  abstract class StaticListType[D <: ListDatum[D, V], V <: Var[V]](
      override val name: QualifiedTypeName,
      override val supertypes: Seq[ListType[_ >: D, _ >: V]],
      override val valueVarType: VarType[V]
  ) extends ListType[D, V](name, supertypes, valueVarType)


  class DynamicListType[V <: Var[V]](
      override val name: QualifiedTypeName,
      override val supertypes: Seq[ListType[_ >: Null, _ >: V]],
      override val valueVarType: VarType[V]
  ) extends ListType[Null, V](name, supertypes, valueVarType)


  abstract class PrimitiveType[D <: PrimitiveDatum[D]](
      override val name: QualifiedTypeName,
      override val supertypes: Seq[PrimitiveType[_ >: D]]
  ) extends DataType[D](name, supertypes) with PrimitiveDataTypeApi


  abstract class StringType[D <: StringDatum[D]](
      override val name: QualifiedTypeName,
      override val supertypes: Seq[StringType[_ >: D]]
  ) extends PrimitiveType[D](name, supertypes) with StringDataTypeApi


  abstract class StaticStringType[D <: StringDatum[D]](
      override val name: QualifiedTypeName,
      override val supertypes: Seq[StringType[_ >: D]]
  ) extends StringType[D](name, supertypes)


}
