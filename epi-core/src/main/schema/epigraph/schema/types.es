namespace epigraph.schema

import epigraph.*

/*abstract */record Type extends Named {
  doc = "Common interface for data type and vartype records";
  /*override */name: QualifiedTypeName
  supertypes: list[TypeRef]
}

vartype ByNameRef default name {
  doc = "Common interface for vartypes representing by-name references";
  name: Name
}

vartype TypeRef extends ByNameRef {
  doc = "Common interface for by-name references to (data or var-) types";
  name: QualifiedTypeName
  type: Type
}


record VarType extends Type {
  doc = "Vartype declaration";
  `default`: TypeMemberRef {
    doc = "Optional type member reference to be used as default one";
  }
  members: list[TypeMember] // TODO rename to `tags`?
}

vartype VarTypeRef extends TypeRef {
  doc = "By-name reference to a vartype";
  type: VarType
}


record TypeMember extends Named {
  doc = "Vartype member declaration";
  name: TypeMemberName
  dataType: DataTypeRef
}

vartype TypeMemberRef extends ByNameRef {
  doc = "By-name reference to vartype member";
  name: TypeMemberName
  member: TypeMember
}


/*polymorphic */record DataType extends Type {
  supertypes: list[DataTypeRef]
  polymorphic: Boolean
  meta: DataTypeRef
}

vartype DataTypeRef extends TypeRef {
  doc = "By-name reference to data type";
  type: DataType
}


record RecordType extends DataType {
  supertypes: list[RecordTypeRef]
  declaredFields: list[Field]
}
vartype RecordTypeRef extends DataTypeRef { type: RecordType }

record Field extends Named {
  name: FieldName
  valueType: VarTypeRef
  `default`: TypeMemberRef
}

record MapType extends DataType {
  supertypes: list[MapTypeRef]
  keyType: DataTypeRef
  valueType: VarTypeRef
}
vartype MapTypeRef extends DataTypeRef { type: MapType }

record ListType extends DataType {
  supertypes: list[ListTypeRef]
  valueType: VarTypeRef
}
vartype ListTypeRef extends DataTypeRef { type: ListType }

record EnumType extends DataType {
  supertypes: list[EnumTypeRef]
//valueType: DataTypeRef?
  values: list[EnumValue]
}

vartype EnumTypeRef extends DataTypeRef { type: EnumType }
record EnumValue extends Named { name: EnumValueName } // TODO value??

/*polymorphic */record PrimitiveType extends DataType { supertypes: list[PrimitiveTypeRef] }
vartype PrimitiveTypeRef extends DataTypeRef { type: PrimitiveType }

record StringType extends PrimitiveType { supertypes: list[StringTypeRef] }
vartype StringTypeRef extends PrimitiveTypeRef { type: StringType }

record IntegerType extends PrimitiveType { supertypes: list[IntegerTypeRef] }
vartype IntegerTypeRef extends PrimitiveTypeRef { type: IntegerType }

record LongType extends PrimitiveType { supertypes: list[LongTypeRef] }
vartype LongTypeRef extends PrimitiveTypeRef { type: LongType }

record DoubleType extends PrimitiveType { supertypes: list[DoubleTypeRef] }
vartype DoubleTypeRef extends PrimitiveTypeRef { type: DoubleType }

record BooleanType extends PrimitiveType { supertypes: list[BooleanTypeRef] }
vartype BooleanTypeRef extends PrimitiveTypeRef { type: BooleanType }
