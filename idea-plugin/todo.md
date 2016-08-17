### High
- Make light-psi a separate module

### Medium
- add SourceRootFinder to detect src/main/epigraph, test/epigraph etc
- Structure popup (ctrl-f12)
- Rename on fields (**In progress**)
- Rename on vartype tags
- On project import: detect `epigraph` as source folders
- Errors highlighter must detect all cases of wrong/useless inheritance:
  - `extends`, `supplements`, `supplement` target and source
    - [x] Circular inheritance
    - [ ] Useless inheritance (type already extended)
    - [ ] Extending type of the wrong kind: e.g a record can't extend a primitive
    - [ ] Extending primitive type of the wrong kind: e.g a long can't extend a string
    - [ ] Extending collection with incompatible element kind: e.g. `List[Foo]` can extend `List[Bar]` only if `Foo` extends `Bar`
  - `supplement` source
    - [ ] Don't warn if at least one of the targets is not a child of source
    

### Low
- Rename projects to `epigraph-idea-plugin` and `epigraph-schema-parser(-common)` ?
- Type ref completion should suggest types from non-imported namespaces, automatically adding imports as needed
- Custom attributes must reference fields in meta-schema (and respect renames, find usages etc)
- Custom attribute values must be based (and validated against) meta-schema. Find usages/refactorings/completion must take it into account

---
## Done
### High
- Remove context-aware lexing, a set of keywords must not change depending on the context

### Medium
- Schema files must only be fully parsed if under (test) source root
- Module dependencies must be respected when resolving references. Tricky for injections: only modules that depend on the current one must be checked

### Low
