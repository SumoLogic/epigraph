#Horizon 1
- [x] add correct errors propagation support to the rest of psi parsers
- [x] operation input projections must be field, not model projections
- [ ] ~~invert 'requried', introduce 'optional' instead?~~
  - [x] ~~op input projections (undone)~~
  - [ ] ~~req output projections~~
- [x] add params to op input projections
- [x] psi parser for req update projections
- [x] psi parser for req delete projections
- [x] data structures for req input projections
- [ ] psi parser for req input projections
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
- [x] op input projections: move model projection out of complex tag projection body to make syntax consistent

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
  
# See also
- See [General todo] (todo.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
