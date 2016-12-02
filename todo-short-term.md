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
  
- [ ] Undertow handler must support all operations
  - [ ] add support for create
  - [ ] add support for update
  - [ ] add support for delete
  - [ ] add support for custom
  
- [ ] Standalone IDL verifier/compiler
  - [ ] Maven plugin
  - [ ] Gradle plugin

# See also
- See [General todo] (todo.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
