- [x] Marshaller for output data
  - [x] Code clean up
  - [x] Add unit tests
  - [ ] ~~Make more generic?~~
  
- [x] Unmarshallers for create/update/custom body
  - [x] abstract generic version
  - [x] guided by req output projection
  - [x] guided by op input projection (for operations called without explicit request projection)
  - [x] guided by req input projection
  - [x] guided by req update projection
  - [ ] ~~guided by req delete projection~~
  
- [x] Move `ReqOutputVarProjection.parenthesized` to `GenVarProjection`
  - [x] Update parsers
  - [x] Update pretty printers
  - [x] Update unmarshallers
  
- [ ] Undertow handler must support all operations
  - [x] add support for create
  - [x] add support for update
  - [x] add support for delete
  - [x] add support for custom
  - [ ] update test server to support/demo all operations
    - [x] read
    - [x] create
    - [ ] update
    - [x] delete
    - [ ] custom
  - [x] enable real streaming
  
- Important items from [general todo] (todo.md)
  - [ ] Add `type.createBuilder(data)` similar to `toImmutable`
    - [ ] Update demo service
  - [ ] Gradle plugin: java generator doesn't detect changes in schema
  - [x] fix `equals` on tags

- Docs
  - Operations
    - [ ] Overview
    - [ ] IDL
    - [ ] Routing
  
- [ ] Standalone IDL verifier/compiler
  - [ ] Maven plugin
  - [ ] Gradle plugin

# See also
- See [General todo] (todo.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
