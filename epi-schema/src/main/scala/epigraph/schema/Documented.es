namespace epigraph . `schema`

record Documented supplements TypeData, TypeMemberData, FieldData, EnumValueData/*, `Documented`*/ {
  doc = "Interface that provides `doc` field"
  doc: String { doc = "Documentation snippet" }
}

//record VarTypeData
//enum VarTypeRef {
//}
//string QualifiedName

supplement TypeData/*, Documented*/ with Documented
