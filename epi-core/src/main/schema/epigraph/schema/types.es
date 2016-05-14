namespace epigraph.schema

import epigraph.*

abstract record Type extends Named {
  doc = "Common interface for data type and vartype records";
  override name: QualifiedTypeName
  abstract supertypes: list[TypeRef]
}

vartype ByNameRef default name {
  doc = "Common interface for vartypes representing by-name references";
  override name: Name
}

vartype TypeRef extends ByNameRef {
  doc = "Common interface for by-name references to (data or var-) types";
  override name: QualifiedTypeName
  type: Type
}


record VarType extends Type {
  doc = "Vartype declaration";
  override supertypes: list[VarTypeRef]
  `default`: TypeMemberRef {
    doc = "Optional type member reference to be used as default one";
  }
  members: list[TypeMember] // TODO rename to `tags`?
}

vartype VarTypeRef extends TypeRef {
  doc = "By-name reference to a vartype";
  override type: VarType
}


record TypeMember extends Named {
  doc = "Vartype member declaration";
  override name: TypeMemberName
  dataType: DataTypeRef
}

vartype TypeMemberRef extends ByNameRef {
  doc = "By-name reference to vartype member";
  override name: TypeMemberName
  member: TypeMember
}


polymorphic record DataType extends Type {
  override supertypes: list[DataTypeRef]
  polymorphic: Boolean
  metaType: DataTypeRef
}

vartype DataTypeRef extends TypeRef {
  doc = "By-name reference to data type";
  override type: DataType
}


record RecordType extends DataType {
  override supertypes: list[RecordTypeRef]
  declaredFields: list[Field]
}
vartype RecordTypeRef extends DataTypeRef { override type: RecordType }

record Field extends Named {
  override name: FieldName
  valueType: VarTypeRef
  `default`: TypeMemberRef
}

record MapType extends DataType {
  override supertypes: list[MapTypeRef]
  keyType: DataTypeRef
  valueType: VarTypeRef
}
vartype MapTypeRef extends DataTypeRef { type: MapType }

record ListType extends DataType {
  override supertypes: list[ListTypeRef]
  valueType: VarTypeRef
}
vartype ListTypeRef extends DataTypeRef { type: ListType }

record EnumType extends DataType {
  override supertypes: list[EnumTypeRef]
//valueType: DataTypeRef?
  values: list[EnumValue]
}

vartype EnumTypeRef extends DataTypeRef { override type: EnumType }
record EnumValue extends Named { override name: EnumValueName } // TODO value??

abstract polymorphic record PrimitiveType extends DataType { override supertypes: list[PrimitiveTypeRef] }
vartype PrimitiveTypeRef extends DataTypeRef { override type: PrimitiveType }

record StringType extends PrimitiveType { override supertypes: list[StringTypeRef] }
vartype StringTypeRef extends PrimitiveTypeRef { override type: StringType }

record IntegerType extends PrimitiveType { override supertypes: list[IntegerTypeRef] }
vartype IntegerTypeRef extends PrimitiveTypeRef { override type: IntegerType }

record LongType extends PrimitiveType { override supertypes: list[LongTypeRef] }
vartype LongTypeRef extends PrimitiveTypeRef { override type: LongType }

record DoubleType extends PrimitiveType { override supertypes: list[DoubleTypeRef] }
vartype DoubleTypeRef extends PrimitiveTypeRef { override type: DoubleType }

record BooleanType extends PrimitiveType { override supertypes: list[BooleanTypeRef] }
vartype BooleanTypeRef extends PrimitiveTypeRef { override type: BooleanType }
