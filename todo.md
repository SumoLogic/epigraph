# General
- [ ] Schema/IDL: enforce folders structure based on namespace?

# Framework
- [ ] JSON unmarshaller

# Type system
- [ ] Enums
- [ ] Rename Data/Var/Union type to some common name

# Schema compiler
- [ ] Annotations support. Should they be inherited? Annotations on annotations?
- [ ] Verbose mode? (propagate it from gradle/maven)
- [ ] (anonymous) types should be collected from *.eidl files too

# Gradle
- [ ] Don't auto-include built-in types (because versioning)
- [ ] package compiled schemas under `epigraph$artifacts` zip entry (see maven plugin)
- [ ] create folder structure based on namespaces, then put original files inside

# Projections
- [ ] see `operations.esc`, bring java classes in sync
- [ ] var projection: add `@Nullable getPathTagProjection`. Same for records and maps
- [ ] unify tag/model projections?

# Operations  
- [ ] see `operations.esc`, restructure operations accordingly

# Service
- [ ] see `operations.esc`, change routing accordingly
