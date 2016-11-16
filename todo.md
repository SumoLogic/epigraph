# General
- [ ] Schema/IDL: enforce folders structure based on namespace?
- [ ] gradle/maven build: add verification that all files have apache license headers

# Framework
- [ ] Fix tails support in JSON output. Data trimmer must be fixed?
- [ ] JSON unmarshaller
  -[ ] recursive data support?
- [ ] Format for parameter values in URL. Currently: GData with single quotes.
- [x] `*PsiParser` should accumulate errors in the context instead of throwing `PsiProcessingException`

# Type system
- [ ] Enums
- [ ] Rename Data/Var/Union type to some common name
- [ ] Restrict map keys to exact declared type only (throw runtime exception - no static checks unfortunately, unless we introduce yet another data flavor that doesn't inherit from supertypes)
  - [ ] Alternatively, define equals for map keys to be declared type-scoped only, implement with wrapper over keys.
- [ ] ~~Allow supplementing Union types with any datum type (applies to all compatible tag types)~~

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
- [ ] Scan `*.eidl` files too
- [ ] Rename `epigraph-schema-compiler-gradle-plugin` to just `gradle-plugin`. Move it together with `common` to top level, to mimic maven plugins structure
- [ ] Produce a list of `EpigraphType`->`JavaClass` mappings artifact (Duplicates task from `Schema compiler` list above)

# Projections
- [x] see `operations.esc`, bring java classes in sync
- [x] var projection: add `@Nullable getPathTagProjection`. Same for records and maps
- [x] remove `includeInDefault` from `Op*` projections, add '*' to request projections instead
- [x] remove default tags from op projections. If var type has default tag (in schema), and operation can build this tag -- then it becomes an implicit default
- [x] add req input projection (for custom operation)
- [ ] `*` support in projections improvement: allow `*` to be present along with other fields (currently it's either `*` or fields list)
- [ ] support for recursive projections
- [ ] Generate projections for specific types

# Operations  
- [x] see `operations.esc`, restructure operations accordingly
- [x] custom operations must support input data

# Service
- [ ] ***see `operations.esc`, change routing accordingly***

# Other
- See [Short-term todo list] (todo-short-term.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`