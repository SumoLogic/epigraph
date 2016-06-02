namespace epigraph.schema

record Documented supplements TypeData, TypeMemberData, FieldData, EnumValueData {
  doc = "Interface that provides `doc` field";
  doc: String { doc = "Documentation snippet"; }
}
