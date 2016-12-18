- [x] EDL compiler should extract type information from resource declarations
- [x] Java codegen should build a `type name -> java class name` index in `META-INF`
- [ ] `TypesResolver` implementation based on the above index file
- [ ] Java codegen should generate instances of `ResourceDeclaration` for all resources (even from dependencies)
- [ ] Java codegen should generate (abstract? empty?) `Resource` implementations for all resources except for dependencies

- Important items from [general todo] (todo.md)
  - [ ] Add `type.createBuilder(data)` similar to `toImmutable` (postponed till there are more use cases)
    - [ ] Update demo service

- Docs
  - Operations
    - [ ] Overview
    - [ ] IDL
    - [ ] Routing

# See also
- See [General todo] (todo.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
