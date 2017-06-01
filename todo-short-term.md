- Federator prerequisites
  - [ ] `toBuilder` on data? Will be tricky because of covariance
  - [x] local service invocation layer
  - [ ] remote service invocation layer
  - [ ] introduce transformers +codegen?
  - [ ] add `api` section to schema, listing a set of exposed resources and if they're federated
    - [ ] codegen something from it
    - [ ] validate at startup that stuff is actually?
    
- [x] http client/server
  - [x] server must correctly set response content type for errors (json/plain text) + test
  - [x] client must correctly specify accepted content types
  - [x] client must correctly read errors based on response content type
  - [x] read
  - [x] create
  - [x] update
  - [x] delete
  - [x] custom
  - tests
    - [x] bad request url
    - [x] non-existing user (should get 404 back with an ErrorValue)
    - [ ] complex url with params (check data escaping)
    - [x] operations with paths
  - [x] tests should cover all 3 servers, one of them (undertow?) in both sync/async modes
  - [x] codegen simple clients for operations? should work for federated responses too if we have client-side federated op projection
    - [ ] ~~read operation with path return type must be path tip type~~ Not possible: don't know at build time where path ends

- [ ] broken gradle build?
- [ ] add 'required' on req output map keys
- [ ] tests & fix for pruner (with support for the above)
- [ ] report scalac bug for java.lang
- [ ] move TypesIndex to another package to avoid scalac bug?
- [ ] transformers interface
- [ ] operations -> transformers ?
- [ ] better release cycle. mvn 3.5.0?
- [ ] Malformed class file [ws/epigraph/projections/abs/AbstractTagProjectionEntry.class] found on compile classpath, which means that this class will cause a compile error if referenced in a source file. Gradle 5.0 will no longer allow malformed classes on compile classpath.

- Docs
  - [x] service quick-start guide
    - [x] simple read
    - [x] search operation
    - [x] create operation
    - [x] update operation
    - [x] delete operation
    - [ ] client
  - Operations
    - [ ] Overview
    - [ ] IDL
    - [ ] Routing
    
# Yegor dependencies
- [ ] non-anonymous map/list types should have qualified type names
  - [ ] enable `AbstractHttpClientTest.testComplexParams` once fixed
- [ ] introduce consistent naming. Type/VarType/DataType/UnionType etc
- [ ] polymorphic builders or projections
  - [ ] update introspection service, see `TypeBuilder`, `DatumTypeBuilder`
- [ ] add enums

# See also
- See [General todo] (todo.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
