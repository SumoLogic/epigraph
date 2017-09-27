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
- [ ] codegen: need better framework for generating imports and imported names
- [x] req projections codegen: add inheritance to input/update/delete projections (model after output projections)
- [ ] req projections codegen: a lot of code duplication, move stuff up (but don't kill extras like 'required' and 'replace')
- [ ] codegen: `_resources/*` package name should be in lower case
- [ ] codegen: primitive `String` setters should accept `CharSequence`
- [ ] codegen: projection parameter getters should only unwrap built-in primitives (but not, say, `UserId`)


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
- [ ] Generated artifacts index format (json? yaml? both?)
  - [ ] Should provide enough information to detect incompatible supplements, etc.
- [ ] Handle top-level package names obscured by in-scope field/variable names in generated code

# Maven plugin
- [x] use slf4j for logging

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
- [ ] op entity projections: no syntax for body (annotations/defaults/...). Use `:{..}`
- [ ] paths: add entity params, make tags optional (so it's possible to have path params without anything else, i.e. without having to change operation type)
- [ ] bug: `(a, b) ~Foo(c) ~Bar $bar = (d)` => `$bar` will include (d,c) but not (a,b)
- [ ] bug? `(+foo)` if foo is an entity type without retro tag, `+` seems to have no effect
- [ ] op parameter projections should have their own reference context, with global/resource input context as a parent
- [ ] `UriComposer`: make sure `+` is added before flagged delete entity projections (+UT)
- [ ] reverse the meaning of `+` (required) on OpInput and ReqOutput projections

# Operations

# Projections refactoring
- [ ] sort out 'path steps' for input/output projections
- [x] format factories: remove duplicating ones
- [x] fix todo in
  - [x] url.bnf (`+` in request)
  - [x] SchemaProjectionPsiParserUtil
  - [x] OperationsPsiParser
  - [x] ResourcesSchemaPsiParser
  - [x] OpInputProjectionsPsiParser
- [x] clean up *ProcessingContext classes | don't bother, they're going away?
- [ ] unify req projections codegens

# Service

# Build
- [x] Try to remove sources copying from `java/schema-parser`, include generated sources directly. See if IDEA will get confused about them. If all is fine: get rid of `maven-resources-plugin`
- Gradle
  -[ ] ~~light-psi assembly: simplify the code, see `build.gradle` notes on using class symbol tables instead of ASM~~
  -[x] light-psi should be versioned (and, potentially, released) separately from the rest of the project, as mvn build does. This would entail adding another `settings-light-psi.gradle` to deploy light-psi to local repo and changing all `project` to usual dependencies
- Maven
- [x] include `examples` in main build
  - [x] maven
  - [x] gradle

- Cleanup
  - [ ] check out artifactory-tools to clean up old light-psi versions
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
- [ ] https://sumologic.slack.com/archives/C2PN5GQS1/p1501890729529761 - `@Doc` annotation, without an import, doesn't show as compilation error in IntelliJ
- [x] https://sumologic.slack.com/archives/C2PN5GQS1/p1502142890265437 - Releases should publish sources too, to make development easier
- [ ] https://sumologic.slack.com/archives/C2PN5GQS1/p1502142552131468 - Clients use a concrete class, instead of interface/ class combo.  This makes dynamic proxying very hard, automatic retries hard, etc.  (Java proxies use interfaces, last I checked)
- [x] Release job needs to publish sources too to make it easy to debug/examine in other projects

# Instrument maven plugin(s) with jacoco agent?
- [ ] e.g.: argLine set to -javaagent:/home/travis/.m2/repository/org/jacoco/org.jacoco.agent/0.7.9/org.jacoco.agent-0.7.9-runtime.jar=destfile=/home/travis/build/SumoLogic/epigraph/target/jacoco.exec
- [ ] then add the .exec to considered ones

# Move java codegen classes into a subpackage
