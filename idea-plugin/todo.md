### High
- Highlight string literals

### Medium
- Rename things:
<<<<<<< HEAD
  - [x] schema-parser-common -> lang-parser-common
  - [x] schema-parser -> lang-parser (with artifact name = epigraph-lang-parser)
  - [x] com.sumologic.epigraph.schema -> io.epigraph.lang
  - [x] schema.bnf -> lang.bnf
    - [x] elementTypePrefx -> `E_`
    - [x] PsiClassPrefix -> 'Epigraph'? Or make it different for Schema/Data/Projection classes?
  - [x] `Schema*` classes to `Epigraph*`, except for `SchemaParser`
  - [ ] *.esc -> *.epigraph? *.epigraph_schema?
- Smart type completion: suggest out of scope types (+add import)
- Smart completion for `default <tag>`, `nodefault` on fields, list elements etc
  - [ ] Don't allow either on datum types (non-vartypes)
  - [ ] Changing default on vartypes is allowed. Defaults on fields (even implicit) must be compatible with parent fields:
  Default on a field = field default > field type default > parent field default > parent field type default 
- add SourceRootFinder to detect src/main/epigraph, test/epigraph etc
- Structure popup (ctrl-f12)
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
- Diagrams support. Bonus: show field types

### Low
- Option to auto optimize imports on code formatting
- Structure view: option to show inherited members
- Auto insert matching quotes/backticks/parens/<> in parameters and data
- Namespace declaration: inspection to warn about namespaces not matching folder structure
- Rename projects to `epigraph-idea-plugin` and `epigraph-schema-parser(-common)` ?
- Type ref completion should suggest types from non-imported namespaces, automatically adding imports as needed
- Custom attributes must reference fields in meta-schema (and respect renames, find usages etc)
- Custom attribute values must be based (and validated against) meta-schema. Find usages/refactorings/completion must take it into account
- File drag&drop: treat as move refactoring (fix namespaces)
- Auto insert imports on paste?

---
## Done
### High
- Rename things:
  - [x] schema-parser-common -> lang-parser-common
  - [x] schema-parser -> lang-parser (with artifact name = epigraph-lang-parser)
  - [x] com.sumologic.epigraph.schema -> io.epigraph.lang
  - [x] schema.bnf -> lang.bnf
    - [x] elementTypePrefx -> `E_`
    - [x] PsiClassPrefix -> 'Epigraph'? Or make it different for Schema/Data/Projection classes?
  - [x] `Schema*` classes to `Epigraph*`, except for `SchemaParser`
  - [x] io.epigraph.lang.schema.**.Schema* -> io.epigraph.lang.**.Epigraph* ?
- Make light-psi a separate module

### Medium

### Low
