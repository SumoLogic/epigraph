### High
- Add support for `*.eidl` files
- [x] `goto class` should not point to files not under source roots, e.g. from `taget` or `classes`

### Medium
- Errors on duplicate fields/tags
- [ ] Warning if folder structure doesn't correspond to namespace (ideally with hotfixes)
- Smart completion for `default <tag>`, `nodefault` on fields, list elements etc
  - [ ] Don't allow either on datum types (non-vartypes)
  - [ ] Changing default on vartypes is allowed. Defaults on fields (even implicit) must be compatible with parent fields:
  Default on a field = field default > field type default > parent field default > parent field type default
- add SourceRootFinder to detect src/main/epigraph, test/epigraph etc
- Rename on fields (**Incomplete**)
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
- Namespace declaration completion: take folder structure into account
- Rename folders on namespace renaming
- Highlight tag types that are vartypes themselve as errors
- Completion: suggest out-of-scope types with auto-import (implementation: copy ref completion logic from `LegacyCompletionContributor` to `SchemaCompletionContributor`, move `SchemaFqnReference.getVariants` there as well, but now with invocation count logic. Add `autoImport` to `beforeCompletion`, see `JavaCompletionContributor:712`)
- Type lookup by fully qualified name (e.g. `epigraph.projections.req.ReqOutputKeysProjection`) fails. Short local name (`ReqOutputKeysProjection`) lookup works.
- UML: supplemented relationships not displayed

### Low
- Option to auto optimize imports on code formatting
- Structure view: option to show inherited members
- Structure popup (ctrl-f12)
- Auto insert matching quotes/backticks/parens/<> in parameters and data
- Namespace declaration: inspection to warn about namespaces not matching folder structure
- Rename project to `epigraph-idea-plugin` ?
- Type ref completion should suggest types from non-imported namespaces, automatically adding imports as needed
- Custom attributes must reference fields in meta-schema (and respect renames, find usages etc)
- Custom attribute values must be based (and validated against) meta-schema. Find usages/refactorings/completion must take it into account
- File drag&drop: treat as move refactoring (fix namespaces)
- Auto insert imports on paste?
- Remove 'No default vartype member specified' warning on data types (field value, list element, map value)
- GoTo symbol contributor

