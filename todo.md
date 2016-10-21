# General
- [ ] Schema/IDL: enforce folders structure based on namespace?

# Framework
- [ ] JSON unmarshaller
- [ ] Format for parameter values in URL. Currently: GData with single quotes.
- [ ] ***see `operations.esc`, restructure projections accordingly***

# Type system
- [ ] Enums
- [ ] Rename Data/Var/Union type to some common name

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
- [ ] ***see `operations.esc`, bring java classes in sync***
- [ ] var projection: add `@Nullable getPathTagProjection`. Same for records and maps
- [ ] unify tag/model projections?

# Operations  
- [ ] ***see `operations.esc`, restructure operations accordingly***

# Service
- [ ] ***see `operations.esc`, change routing accordingly***

# Other
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`