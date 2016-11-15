#Horizon 1
- [ ] add input (projection) support to custom operations
- [ ] add optional input projection support to create operations
- [ ] url parsers
  - [x] read
  - [ ] create
  - [x] udpate
  - [ ] delete
  - [ ] custom
- [ ] new routing algorithm
  - [x] generic
  - [x] reads
  - [ ] create
  - [ ] update
  - [ ] delete

#Horizon 2
- [ ] Unmarshallers for create/update/custom body
  - [ ] must be guidable by op input projection
  - [ ] must be guidable by req input projection
  - [ ] must be guidable by req update projection
  - [ ] must be guidable by req delete projection
- [ ] Undertow handler must support all operations
  - [ ] add support for create
  - [ ] add support for update
  - [ ] add support for delete
  - [ ] add support for custom
  
#Horizon 3
- [ ] Standalone IDL verifier/compiler
  - [ ] Maven plugin
  - [ ] Gradle plugin

#Low priority
- [ ] Unify req projections pretty printers, there's lots of code duplication
- [ ] Unify op projections pretty printers, there's lots of code duplication
  
# See also
- See [General todo] (todo.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
