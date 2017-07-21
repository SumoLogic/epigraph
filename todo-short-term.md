- [x] add `Assemblers` codegen
- [x] update reflection implementation
- [ ] rest of op parsers: named tails normalization
- [ ] req projections codegen: add inheritance for non-output projections
- [ ] req projections codegen: remove `normalizedFor` methods
- [ ] update library example & docs

- [ ] new annotations
  - [x] add to op params
  - [x] add to op entity projections
  - [x] add (delegating) annotations to fields and tags
  - [ ] update reflection service

- [ ] allow paths to end with model types (prereq. for fed. ?)
  
- Federator
  - [x] support named projections on the top level (not inside service)
  - [x] add transformers to schema
    - [x] codegen implementation stubs
  - [ ] operations -> transformers via annotations
  
  - [ ] `toBuilder` on data? Will be tricky because of covariance
  - [ ] add `api` section to schema, listing a set of exposed resources and if they're federated
    - [ ] codegen something from it
    - [ ] validate at startup that stuff is actually?
    
# Yegor dependencies
- [ ] introduce consistent naming. Type/VarType/DataType/UnionType etc. Entity/Model types?
- [ ] add enums

# See also
- See [General todo] (todo.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
