# General
- [ ] Schema/IDL: enforce folders structure based on namespace?

# Framework
- [ ] JSON unmarshaller
- [ ] Format for parameter values in URL. Currently: GData with single quotes.
- [ ] `*PsiParser` should accumulate errors in the context instead of throwing `PsiProcessingException`

# Type system
- [ ] Enums
- [ ] Rename Data/Var/Union type to some common name
- [ ] Restrict map keys to exact declared type only (throw runtime exception - no static checks unfortunately, unless we introduce yet another data flavor that doesn't inherit from supertypes)
  - [ ] Alternatively, define equals for map keys to be declared type-scoped only, implement with wrapper over keys.

# Schema compiler
- [ ] Annotations support. Should they be inherited? Annotations on annotations?
- [ ] Verbose mode? (propagate it from gradle/maven)
- [ ] (anonymous) types should be collected from `*.eidl` files too

# Maven plugin
- [ ] Scan `*.eidl` files too

# Gradle plugin
- [x] Don't auto-include built-in types (because versioning)
- [x] package compiled schemas under `epigraph$artifacts` zip entry (see maven plugin)
- [x] create folder structure based on namespaces, then put original files inside
- [ ] Scan `*.eidl` files too
- [ ] Rename `epigraph-schema-compiler-gradle-plugin` to just `gradle-plugin`. Move it together with `common` to top level, to mimic maven plugins structure

# Projections
- [x] see `operations.esc`, bring java classes in sync
- [x] var projection: add `@Nullable getPathTagProjection`. Same for records and maps
- [ ] remove default tags from op projections. If var type has default tag (in schema), and operation can build this tag -- then it becomes an implicit default

# Operations  
- [ ] ***see `operations.esc`, restructure operations accordingly***

# Service
- [ ] ***see `operations.esc`, change routing accordingly***

# Other
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`