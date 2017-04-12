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
- [ ] Feature: add support for resource overlays? adding operations to other resources (e.g. with paths)
- [ ] generated builders should have some form of inheritance
- [ ] `Operation` instances must be validated befor execution, e.g. input data must match input projection, all requried parts must be present etc
- [ ] operations codegen: process(inputData) parameter should be non-null if marked as required in the projection

# Type system
- [ ] Enums
- [ ] Restrict map keys to exact declared type only (throw runtime exception - no static checks unfortunately, unless we introduce yet another data flavor that doesn't inherit from supertypes)
  - [ ] Alternatively, define equals for map keys to be declared type-scoped only, implement with wrapper over keys.
- [ ] ~~Allow supplementing Union types with any datum type (applies to all compatible tag types)~~
- [ ] **Add `type.createBuilder(data)` similar to `toImmutable`**
- [ ] Feature: Introduce real epigraph (record?) type for holding error values
- [ ] Feature: Add `abstract` (`any`?) datum type, extensible by any other datum type. Translate to interface in codegen. Tails can't be normalized/mern.segments[i];
- [ ] Feature: add `final` on fields. Codegen: record builders should implement special `FinalFields` interfaces which should inherit each other

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
- [ ] use slf4j for logging

# Gradle plugin
- [ ] use slf4j for logging

# Projections
- [ ] `*` support in projections improvement: allow `*` to be present along with other fields (currently it's either `*` or fields list)
- [ ] figure out remaining `mergeOpTails` (done for output projections, so should be easy)
  - [ ] update parser
  - [ ] input parser
  - [ ] delete parser
- [ ] Perform full tails normalization in parsers
- [x] Unify req projections pretty printers, there's lots of code duplication
- [x] Unify op projections pretty printers, there's lots of code duplication
- [ ] ~~Add meta-projection to req input model projections?~~ Decided not needed for now.
- [ ] generated req projections: cache normalized projections?
- [x] fix projections pretty printer for records, see OpOutputProjectionsTest.testParsing
- [ ] ~~remove type information from projections?~~
  - [ ] ~~record projections should contain a String->FP, not String->FPE map~~
- [x] support for recursive projections
- [x] correct `equals` support, see todo on `AbstractVarProjection.equals`
- [x] enable real named model projections. Currently they can't be used for tags, meta or model tails
- [x] allow attaching tails to references? e.g. `$foo ~Bar(..)`
- [ ] enable model projection references for req projections
- [ ] Feature: Add `throws` to op projections: `:someModel throws ( Error(message) ~MyError(code) )`
- [ ] Feature: Add `catch` to req projections: `:someModel catch ( Error(message) ~MyError(code) )`. This should guide marshallers/unmarshallers
- [ ] handle cases like `(foo $rec = ( foo $rec ) ~Bar ( foo ( baz ) ) )`, see AbstractVarProjection:mergeTags (allow merging recursive and non-recursive projections)
- [ ] allow merging multiple recursive projections (seems to be a hard task)
- [ ] generated req projections should have equals/hashcode (use `GenProjectionsComparator`)

# Operations

# Service
- [ ] Add (limited?) support for passing operation name via query parameter

# Build
- [ ] Fix circular build problem. Depends on Schema compiler/proper resouces compilation task above (~ a month of work)
- [x] Try to remove sources copying from `java/schema-parser`, include generated sources directly. See if IDEA will get confused about them. If all is fine: get rid of `maven-resources-plugin`
- Gradle
  -[ ] ~~light-psi assembly: simplify the code, see `build.gradle` notes on using class symbol tables instead of ASM~~
  -[x] light-psi should be versioned (and, potentially, released) separately from the rest of the project, as mvn build does. This would entail adding another `settings-light-psi.gradle` to deploy light-psi to local repo and changing all `project` to usual dependencies
- Maven
- [ ] include `examples` in main build
  - [ ] maven
  - [x] gradle

- Cleanup
  - [ ] AnonMapType/AnonListType: why immediateSupertypes are based on valueType's immediateSupertypes?
  - [ ] DataType(Api): type should be VarType ?
  - [ ] Rename Data/Var/Union type to some common name

# Other
- See [Short-term todo list] (todo-short-term.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
