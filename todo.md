# General
- [ ] Schema/IDL: enforce folders structure based on namespace?
- [ ] gradle/maven build: add verification that all files have apache license headers

# Framework
- JSON marshaller
  -[x] metadata support
  -[ ] recursive data support
- JSON unmarshaller
  -[x] metadata support
  -[ ] recursive data support
- [ ] Format for parameter values in URL. Currently: GData with single quotes.
- [ ] `ReqDelete` psi parser must ensure that leaf items have `op.canDelete` set to `true`
- [x] Replace `List<PsiProcessingError>` by `PsiProcessingContext`, so we can pass more context information for error messages
- [ ] Recursive GData support? GData snippets in schema?
- [ ] data trimmer
  - [ ] generify
  - [ ] should support recursive data
  - [ ] add tests
  - [ ] invocation layer should run it on operation inputs/outputs for in-process calls

# Type system
- [ ] Enums
- [ ] Rename Data/Var/Union type to some common name
- [ ] Restrict map keys to exact declared type only (throw runtime exception - no static checks unfortunately, unless we introduce yet another data flavor that doesn't inherit from supertypes)
  - [ ] Alternatively, define equals for map keys to be declared type-scoped only, implement with wrapper over keys.
- [ ] ~~Allow supplementing Union types with any datum type (applies to all compatible tag types)~~
- [ ] **Add `type.createBuilder(data)` similar to `toImmutable`**
- [ ] Introduce real epigraph (record?) type for holding error values
- [ ] Add `abstract` (`any`?) datum type, extensible by any other datum type. Translate to interface in codegen. Tails can't be normalized/merged

# Schema compiler
- [ ] Annotations support. Should they be inherited? Annotations on annotations?
- [ ] Verbose mode? (propagate it from gradle/maven)
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

# Gradle plugin

# Projections
- [ ] `*` support in projections improvement: allow `*` to be present along with other fields (currently it's either `*` or fields list)
- [ ] support for recursive projections
- [ ] figure out remaining `mergeOpTails` (done for output projections, so should be easy)
  - [ ] update parser
  - [ ] input parser
  - [ ] delete parser
- [ ] Perform full tails normalization in parsers
- [ ] Unify req projections pretty printers, there's lots of code duplication
- [ ] Unify op projections pretty printers, there's lots of code duplication
- [ ] Add `throws` to op projections: `:someModel throws ( Error(message) ~MyError(code) )`
- [ ] Add `catch` to req projections: `:someModel catch ( Error(message) ~MyError(code) )`. This should guide marshallers/unmarshallers
- [ ] ~~Add meta-projection to req input model projections?~~ Decided not needed for now.
- [ ] generated req projections: cache normalized projections?
- [x] fix projections pretty printer for records, see OpOutputProjectionsTest.testParsing
- [ ] remove type information from projections?
  - [ ] record projections should contain a String->FP, not String->FPE map
- [ ] correct `equals` support, see todo on `AbstractVarProjection.equals`

# Operations

# Service

# Build
  - [ ] Fix circular build problem. Depends on Schema compiler/proper resouces compilation task above (~ a month of work)
  - [x] Try to remove sources copying from `java/schema-parser`, include generated sources directly. See if IDEA will get confused about them. If all is fine: get rid of `maven-resources-plugin`
  - Gradle
    -[ ] ~~light-psi assembly: simplify the code, see `build.gradle` notes on using class symbol tables instead of ASM~~
    -[x] light-psi should be versioned (and, potentially, released) separately from the rest of the project, as mvn build does. This would entail adding another `settings-light-psi.gradle` to deploy light-psi to local repo and changing all `project` to usual dependencies
  - Maven

# Other
- See [Short-term todo list] (todo-short-term.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
