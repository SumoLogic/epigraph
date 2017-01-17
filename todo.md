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
- [x] (anonymous) types should be collected from `*.eidl` files too
- [x] Build an index of types in `META-INF`. Type name to schema file?
- [ ] Proper resources compilation (port op projections/resource decls to scala/ctypes)
  - [x] Port generic projections to scala
  - [x] Port `GData` to scala/ctypes
  - [ ] Port op projections to scala/ctypes
  - [ ] Port op projection psi parsers to scala/ctypes
  - [ ] Port operations declarations to scala/ctypes
  - [ ] Port operations declarations psi parsers to scala/ctypes
  - [ ] Port `ResourcesSchema` psi parsers to scala/ctypes
  - [ ] Change codegen to use scala versions
  - [ ] Remove ctypes -> types API bridge
  - [ ] Remove types API?
  - [ ] Revert gradle/maven build changes added to avoid circular dependencies

# Maven plugin
- [x] Produce a list of `EpigraphType`->`JavaClass` mappings artifact (Duplicates task from `Schema compiler` list above)

# Gradle plugin
- [x] Produce a list of `EpigraphType`->`JavaClass` mappings artifact (Duplicates task from `Schema compiler` list above)

# Projections
- [ ] `*` support in projections improvement: allow `*` to be present along with other fields (currently it's either `*` or fields list)
- [ ] support for recursive projections
- [ ] Generate projection classes for specific types. For both req and op! So it's possibly to easly access things like permissions attached to op
- [ ] figure out remaining `mergeOpTails` (done for output projections, so should be easy)
  - [ ] update parser
  - [ ] input parser
  - [ ] delete parser
- [ ] Perform full tails normalization in parsers
- [ ] Unify req projections pretty printers, there's lots of code duplication
- [ ] Unify op projections pretty printers, there's lots of code duplication
- [ ] Add `throws` to op projections: `someField throws ( Error(message) ~MyError(code) )`
- [ ] Add `catch` to req projections: `someField throws ( Error(message) ~MyError(code) )`
- [x] Bug: meta-projection type should be `ModelProjection` (currently it's map for maps, list for lists etc)

# Operations
-[x] Operations code gen
  -[x] Op input projections code gen
    -[x] Add support for defaults
  -[x] Op output projections code gen
  -[x] Op delete projections code gen

# Service

# Build
  - Gradle
    -[ ] light-psi assembly: simplify the code, see `build.gradle` notes on using class symbol tables instead of ASM
    -[ ] light-psi should be versioned (and, potentially, released) separately from the rest of the project, as mvn build does. This would entail adding another `settings-light-psi.gradle` to deploy light-psi to local repo and changing all `project` to usual dependencies
  - Maven

# Other
- See [Short-term todo list] (todo-short-term.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
