- Federator prerequisites
  - [ ] `toBuilder` on data
  - [ ] remote service invocation layer
  - [ ] introduce transformers +codegen?
  - [ ] add `api` section to schema, listing a set of exposed resources and if they're federated
    - [ ] codegen something from it
    - [ ] validate at startup that stuff is actually?

- Docs
  - [ ] service quick-start guide
    - [x] simple read
    - [ ] search operation
    - [ ] create operation
    - [ ] update operation
    - [ ] delete operation
  - Operations
    - [ ] Overview
    - [ ] IDL
    - [ ] Routing
    
- [ ] update introspection service using builder setters
- [ ] add request/response validation
  - [ ] Requried stuff must be present. 
    - [ ] Data validation (input/output)
    - [ ] GData validation (params)
  - [ ] Codegen should build @NotNull things (params). 
  - [ ] Update library example and wiki
- [ ] introduce consistent naming. Type/VarType/DataType/UnionType etc
- [ ] add enums
- [ ] jetty-based server

# See also
- See [General todo] (todo.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
