#Horizon 1
- [x] add input (projection) support to custom operations
- [x] add optional input projection support to create operations
- [x] url parsers
  - [x] read
  - [x] create
  - [x] udpate
  - [x] delete
  - [x] custom
- [ ] new routing algorithm
  - [x] generic
  - [x] reads
  - [x] create
  - [ ] update
  - [ ] delete
- [x] Req input projections parsing: raise errors if `required` parts are missing
- [ ] Remove remaining usages of deprecated `PsiProcessingException` constructor

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
