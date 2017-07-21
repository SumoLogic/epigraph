# General
- [ ] Schema/IDL: enforce folders structure based on namespace?
- [ ] gradle/maven build: add verification that all files have apache license headers

# Framework
- [ ] `ReqDelete` psi parser must ensure that leaf items have `op.canDelete` set to `true`
- [ ] Recursive GData support? GData snippets in schema?
- [ ] data trimmer (currently unused since marshallers trim data anyways)
  - [ ] generify
  - [ ] should support recursive data
  - [ ] add tests
  - [ ] invocation layer should run it on operation inputs/outputs for in-process calls
- [ ] Feature: add support for resource overlays? adding operations to other resources (e.g. with paths)
- [ ] operations codegen: process(inputData) parameter should be non-null if marked as required in the projection
- [ ] `OperationFilterChains`
  - [ ] add filters that ensure req<->op validity
  - [ ] add filter that ensures map keys validity w.r.t. op. map key model projection 
        e.g. `weirdResource: map[UserRecord,UserRecord] ... outputProjection [projection (firstName, +lastName)](..)`
        should only allow map keys with optional `firstName` and mandatory `lastName` and no other fields, both op. input and output
- [ ] Feature: Wrap `Data` in Futures to support async operations
- [ ] Performance: true async support for http
  - [ ] `FormatReader` must use push, not pull style: feed it with data chunks until the whole object is ready
  - [ ] server and client `ServerProtocol.read*` methods must return `CompletableFuture` of results and stay async
  - [ ] `AbstractRemoteOperationInvocation` must use new push readers instead of in-memory buffering `BasicAsyncResponseConsumer`
  - See also: https://github.com/FasterXML/jackson-core/issues/57
- [ ] remove `requestParams` from requests/URLs?
- [ ] generated collections.add: take native primitives as arguments and do auto-wrapping
- [ ] find a way to mark `entity` typed fields/keys as errors, for instance 404
- [x] better implementation of schema annotations. Get inspiration from Java annotations? e.g.
    ```
        string MyAnnotation // taget? default?
        record SomeRecord {
          @MyAnnotation "foo"
        }  
    ```
    Figure out injections: how to add annotations to existing types/fields.
- [ ] req output projections codegen: use `ImportManager` to get more readable projections code
- [ ] req projections codegen: add inheritance to input/update/delete projections (model after output projections)


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
  - [ ] Revert gradle/maven build changes added to avoid circular dependencies (see `compiler` build files)
- [ ] Check that anonymous map keys are datum (not data) type

# Maven plugin
- [ ] use slf4j for logging

# Gradle plugin
- [ ] use slf4j for logging
- [ ] allow to combine schema and java in one module. Make `java/codegen-test` work

# Projections
- [ ] `*` support in projections improvement: allow `*` to be present along with other fields (currently it's either `*` or fields list)
- [ ] ~~Add meta-projection to req input model projections?~~ Decided not needed for now.
- [ ] Feature: Add `throws` to op projections: `:someModel throws ( Error(message) ~MyError(code) )`
- [ ] Feature: Add `catch` to req projections: `:someModel catch ( Error(message) ~MyError(code) )`. This should guide marshallers/unmarshallers
- [ ] handle cases like `(foo $rec = ( foo $rec ) ~Bar ( foo ( baz ) ) )`, see AbstractVarProjection:mergeTags (allow merging recursive and non-recursive projections)
- [ ] allow merging multiple recursive projections (seems to be a hard task)
- [ ] generated req projections should have equals/hashcode (use `GenProjectionsComparator`)
- [ ] req update projections should support paths (trunk), with trimmed input data on the wire
- [ ] op input projections: move `required` from fields/map keys to vars for consistency reasons
- [ ] key projections: rename to specs? we now have key model projections inside op key projections which creates naming mess

# Operations

# Service

# Build
- [x] Try to remove sources copying from `java/schema-parser`, include generated sources directly. See if IDEA will get confused about them. If all is fine: get rid of `maven-resources-plugin`
- Gradle
  -[ ] ~~light-psi assembly: simplify the code, see `build.gradle` notes on using class symbol tables instead of ASM~~
  -[x] light-psi should be versioned (and, potentially, released) separately from the rest of the project, as mvn build does. This would entail adding another `settings-light-psi.gradle` to deploy light-psi to local repo and changing all `project` to usual dependencies
- Maven
- [ ] include `examples` in main build
  - [ ] maven
  - [x] gradle

- Cleanup
  - [x] AnonMapType/AnonListType: why immediateSupertypes are based on valueType's immediateSupertypes?
        Because, e.g. map[Foo, Bar] extends map[Foo, BarSuper1], map[Foo, BarSuper2], etc.
  - [ ] DataType(Api): type should be EntityType ?
  - [ ] Rename Data/Var/Union type to some common name

# Other
- See [Short-term todo list] (todo-short-term.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`

# Unsorted Things From Chris
- [ ] https://sumologic.slack.com/archives/C2PN5GQS1/p1500590191278104 - If you give a `resource`'s `outputProjection` a Record or a `*` instead of a list of fields, it fails to compile.  IntelliJ does not highlight this
- [x] https://sumologic.slack.com/archives/C2PN5GQS1/p1500590236292212 - The blue font in the compilation error is very difficult to read on the default iTerm theme (black background)
