# General
- [ ] Schema/IDL: enforce folders structure based on namespace?
- [ ] gradle/maven build: add verification that all files have apache license headers

# Framework
- [x] Fix tails support in JSON output. Data trimmer must be fixed?
- JSON marshaller
  -[ ] metadata support
  -[ ] recursive data support
- JSON unmarshaller
  -[ ] metadata support
  -[ ] recursive data support
- [ ] Format for parameter values in URL. Currently: GData with single quotes.
- [x] `*PsiParser` should accumulate errors in the context instead of throwing `PsiProcessingException`
- [ ] `ReqDelete` psi parser must ensure that leaf items have `op.canDelete` set to `true`
- [ ] Replace `List<PsiProcessingError>` by `PsiProcessingContext`, so we caontext informatio // todo get rid of second pair of ()n there

# Type system
- [ ] Enums
- [ ] Rename Data/Var/Union type to some common name
- [ ] Restrict map keys to exact declared type only (throw runtime exception - no static checks unfortunately, unless we introduce yet another data flavor that doesn't inherit from supertypes)
  - [ ] Alternatively, define equals for map keys to be declared type-scoped only, implement with wrapper over keys.
- [ ] ~~Allow supplementing Union types with any datum type (applies to all compatible tag types)~~
- [ ] Add `type.createBuilder(data)` similar to `toImmutable`
- [ ] Introduce real epigraph (record?) type for holding error values

# Schema compiler
- [ ] Annotations support. Should they be inherited? Annotations on annotations?
- [ ] Verbose mode? (propagate it from gradle/maven)
- [ ] (anonymous) types should be collected from `*.eidl` files too
- [ ] Build an index of types in `META-INF`. Type name to schema file?

# Maven plugin
- [ ] Scan `*.eidl` files too
- [ ] Produce a list of `EpigraphType`->`JavaClass` mappings artifact (Duplicates task from `Schema compiler` list above)

# Gradle plugin
- [x] Don't auto-include built-in types (because versioning)
- [x] package compiled schemas under `epigraph$artifacts` zip entry (see maven plugin)
- [x] create folder structure based on namespaces, then put original files inside
- [ ] **High: java generator doesn't detect changes in schema**
- [ ] Scan `*.eidl` files too
- [ ] Rename `epigraph-schema-compiler-gradle-plugin` to just `gradle-plugin`. Move it together with `common` to top level, to mimic maven plugins structure
- [ ] Produce a list of `EpigraphType`->`JavaClass` mappings artifact (Duplicates task from `Schema compiler` list above)

# Projections
- [x] see `operations.esc`, bring java classes in sync
- [x] var projection: add `@Nullable getPathTagProjection`. Same for records and maps
- [x] remove `includeInDefault` from `Op*` projections, add '*' to request projections instead
- [x] remove default tags from op projections. If var type has default tag (in schema), and operation can build this tag -- then it becomes an implicit default
- [x] add req input projection (for custom operation)
- [ ] `*` support in projections improvemlow `*` to be pre // todo get rid of second pair of ()sent along with other fields (currently it's either `*` or fields list)
- [ ] OpInputKey projections should support the notion of required/forbidden/optional keys, and ReqInput parser should respect/validate it
- [ ] support for recursive projections
- [ ] Generate projection classes for specific types
- [ ] figure out remaining `mergeOpTails` (done for output projections, so should be easy)
  - [ ] update parser
  - [ ] input parser
  - [ ] delete parser
- [ ] Perform full tails normalization in parsers
- [x] introduce `AbstractReqFieldProjection` with `ReqParams`, similar to `AbstractOpFieldProjection`
- [ ] Unify req projections pretty printers, there's lots of code duplication
- [ ] Unify op projections pretty printers, there's lots of code duplication
- [x] Allow to use optional `(``)` parenthesis around var projections for disambiguation; don't require these for collection element projections (done for collection elements)
- [ ] Add `throws` to op projections: `someField throws ( Error(message) ~MyError(code) )`
- [ ] Add `catch` to req projections: `someField throws ( Error(message) ~MyError(code) )`

# Operations  
- [x] see `operations.esc`, restructure operations accordingly
- [x] custom operations must support input data

# Service
- [x] see `operations.esc`, change routing accordingly

# Other
- See [Short-term todo list] (todo-short-term.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
