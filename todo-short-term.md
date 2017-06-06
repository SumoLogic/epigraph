- Federator
  - [ ] support named projections on the top level (not inside service)
  - [ ] add transformers to schema
    - [ ] codegen implementation stubs
  - [ ] operations -> transformers via annotations
  
  - [ ] `toBuilder` on data? Will be tricky because of covariance
  - [ ] add `api` section to schema, listing a set of exposed resources and if they're federated
    - [ ] codegen something from it
    - [ ] validate at startup that stuff is actually?
    
# Yegor dependencies
- [ ] introduce consistent naming. Type/VarType/DataType/UnionType etc
- [ ] polymorphic builders or projections
  - [ ] update introspection service, see `TypeBuilder`, `DatumTypeBuilder`
- [ ] add enums

# See also
- See [General todo] (todo.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
