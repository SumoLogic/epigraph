namespace epigraph.schema

import epigraph.*

vartype ByNameRef default name {
  doc = "Common interface for vartypes representing by-name references";
  name: Name
}


abstract record TypeData extends Named {
  doc = "Common interface for data type and vartype records";
  override name: QualifiedTypeName
  abstract supertypes: list[TypeRef]
}

vartype TypeRef extends ByNameRef {
  doc = "Common interface for by-name references to (data or var-) types";
  override name: QualifiedTypeName
  type: TypeData
}


record VarTypeData extends TypeData {
  doc = "Vartype declaration";
  override supertypes: list[VarTypeRef]
  `default`: TypeMemberRef {
    doc = "Optional type member reference to be used as default one";
  }
  members: list[TypeMemberData] // TODO rename to `tags`?
}

vartype VarTypeRef extends TypeRef {
  doc = "By-name reference to a vartype";
  override type: VarTypeData
}


record TypeMemberData extends Named {
  doc = "Vartype member declaration";
  override name: TypeMemberName
  dataType: DataTypeRef
}

vartype TypeMemberRef extends ByNameRef {
  doc = "By-name reference to vartype member";
  override name: TypeMemberName
  member: TypeMemberData
}


polymorphic record DataTypeData extends TypeData {
  override supertypes: list[DataTypeRef]
  polymorphic: Boolean
  metaType: DataTypeRef
}

vartype DataTypeRef extends TypeRef {
  doc = "By-name reference to data type";
  override type: DataTypeData
}


record RecordTypeData extends DataTypeData {
  override supertypes: list[RecordTypeRef]
  declaredFields: list[FieldData]
}

vartype RecordTypeRef extends DataTypeRef { override type: RecordTypeData }

record FieldData extends Named {
  override name: FieldName
  valueType: VarTypeRef
  `default`: TypeMemberRef
}


record MapTypeData extends DataTypeData {
  override supertypes: list[MapTypeRef]
  keyType: DataTypeRef
  valueType: VarTypeRef
}

vartype MapTypeRef extends DataTypeRef { type: MapTypeData }


record ListTypeData extends DataTypeData {
  override supertypes: list[ListTypeRef]
  valueType: VarTypeRef
}

vartype ListTypeRef extends DataTypeRef { type: ListTypeData }


record EnumTypeData extends DataTypeData {
  override supertypes: list[EnumTypeRef]
//valueType: DataTypeRef?
  values: list[EnumValueData]
}

vartype EnumTypeRef extends DataTypeRef { override type: EnumTypeData }

record EnumValueData extends Named { override name: EnumValueName } // TODO value??


abstract polymorphic record PrimitiveTypeData extends DataTypeData { override supertypes: list[PrimitiveTypeRef] }
vartype PrimitiveTypeRef extends DataTypeRef { override type: PrimitiveTypeData }


record StringTypeData extends PrimitiveTypeData { override supertypes: list[StringTypeRef] }
vartype StringTypeRef extends PrimitiveTypeRef { override type: StringTypeData }


record IntegerTypeData extends PrimitiveTypeData { override supertypes: list[IntegerTypeRef] }
vartype IntegerTypeRef extends PrimitiveTypeRef { override type: IntegerTypeData }


record LongTypeData extends PrimitiveTypeData { override supertypes: list[LongTypeRef] }
vartype LongTypeRef extends PrimitiveTypeRef { override type: LongTypeData }


record DoubleTypeData extends PrimitiveTypeData { override supertypes: list[DoubleTypeRef] }
vartype DoubleTypeRef extends PrimitiveTypeRef { override type: DoubleTypeData }


record BooleanTypeData extends PrimitiveTypeData { override supertypes: list[BooleanTypeRef] }
vartype BooleanTypeRef extends PrimitiveTypeRef { override type: BooleanTypeData }
