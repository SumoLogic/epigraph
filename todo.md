# General
- [ ] Schema/IDL: enforce folders structure based on namespace?
- [ ] gradle/maven build: add verification that all files have apache license headers
- [ ] release task should create correctly named idea plugin
  - [ ] and ideally publish it to github
- [ ] upgrade gradle to 4.x

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
- [ ] better support `requestParams` in requests/URLs. Should be guided by op declaraions
- [ ] generated collections.add: take native primitives as arguments and do auto-wrapping
- [ ] find a way to mark `entity` typed fields/keys as errors, for instance 404
- [x] better implementation of schema annotations. Get inspiration from Java annotations?
  - [ ] Figure out injections: how to add annotations to existing types/fields.
- [x] codegen: need better framework for generating imports and imported names
  - try building strings with markers inside: "$imports", "$typeref{fqn,shortname}". Keep in mind that types can be
    nested, e.g. "some.package.Foo<some.other.Bar>"
- [ ] req projections codegen: a lot of code duplication, move stuff up (but don't kill extras like 'required' and 'replace')
- [x] BUG in path+data pruner, see `AbstractHttpServerTest::testRequiredInsideMapInPath`

# Type system
- [ ] Enums
- [ ] Restrict map keys to exact declared type only (throw runtime exception - no static checks unfortunately, unless we introduce yet another data flavor that doesn't inherit from supertypes)
  - [ ] Alternatively, define equals for map keys to be declared type-scoped only, implement with wrapper over keys.
- [ ] **Add `type.createBuilder(data)` similar to `toImmutable`**
- [ ] Feature: Introduce real epigraph (record?) type for holding error values
- [ ] Feature: Add `abstract` (`any`?) datum type, extensible by any other datum type. Translate to interface in codegen. Tails can't be normalized/mern.segments[i];
- [ ] Feature: add `final` on fields. Codegen: record builders should implement special `FinalFields` interfaces which should inherit each other

# Schema compiler
- [ ] Verbose mode? (propagate it from gradle/maven)
- [ ] Check that anonymous map keys are datum (not data) type
- [ ] Generated artifacts index format (json? yaml? both?)
  - [ ] Should provide enough information to detect incompatible supplements, etc.
- [ ] Handle top-level package names obscured by in-scope field/variable names in generated code

# Maven plugin

# Gradle plugin
- [ ] use slf4j for logging
- [ ] allow to combine schema and java in one module. Make `java/codegen-test` work

# Projections
- [ ] `...` support in projections improvement: allow `...` to be present along with other fields (currently it's either `...` or fields list)
- [ ] ~~Add meta-projection to req input model projections?~~ Decided not needed for now.
- [ ] Feature: Add `throws` to op projections: `:someModel throws ( Error(message) ~MyError(code) )`
- [ ] Feature: Add `catch` to req projections: `:someModel catch ( Error(message) ~MyError(code) )`. This should guide marshallers/unmarshallers
- [ ] handle cases like `(foo $rec = ( foo $rec ) ~Bar ( foo ( baz ) ) )`, see AbstractVarProjection:mergeTags (allow merging recursive and non-recursive projections)
- [ ] HARD allow merging multiple recursive projections
- [ ] generated req projections should have equals/hashcode (use `GenProjectionsComparator`)
- [ ] key projections: rename to specs? we now have key model projections inside op key projections which creates naming mess
- [ ] ~~op entity projections: no syntax for body (annotations/defaults/...). Use `:{..}`~~
- [ ] paths: ~~add entity params,~~ make tags optional (so it's possible to have path params without anything else, i.e. without having to change operation type)
- [ ] bug? `(+foo)` if foo is an entity type without retro tag, `+` seems to have no effect
- [ ] `UriComposer`: make sure `+` is added before flagged delete entity projections (+UT)
- [ ] reverse the meaning of `+` (required) on OpInput and ReqOutput projections
  - do NOT mark default projection parts as `required` though
- [ ] consider removing model kinds separation from grammar/psi to allow more flexible syntax, e.g. `friendsMap * ( foo )`
- [ ] (?) post-parsing req projections validations/transformations should (also?) actually happen
      in filter chains, otherwise it won't affect projections constructed using builders.
      On the other hand if it only happens in filter chains it will be hard to report errors with pointers to the original parsed string.
- [ ] reverse the meaning of `+` (required) on OpInput and ReqOutput projections. In the future: nullable fields should not be included in default req output
- [ ] implement https://github.com/SumoLogic/epigraph/wiki/default-projections
  - [ ] `ReqBasicProjectionPsiParser` should take `Nullable` data, traverse it and pass to the default projection constructor
  - [ ] request body should be marshalled using (expanded) request projection
  - [ ] request body should be deserialized using op projection
  - [ ] req input projection should be parsed after body deserialization, see step 1
  - [ ] UT: `AbstractHttpClientTest::testCustomWithPath`, add another one with param on `list[]` on `create` operation
- [ ] do something about nullable/optional in schema/projections
  - make generated records throw errors if asked for non-existing fields?
  - then add `@NotNull` on fields in schema, translated into `@NotNull` on generated records
  - https://sumologic.slack.com/archives/D0JPD1FKN/p1507142820000118
  - default req output projections should not include nullable fields (otherwise whole requests will start failing)
- [ ] op parameter projections should have their own reference context, with global/resource input context as a parent
- [ ] bug: there seems to be a race in projections codegen. Uncomment parallel execution in `EpigraphJavaGenerator` and
  do `gradle --rerun-tasks :epigraph-builtin-services-service:compileJava`, sometimes `OutputDatumTypeProjection` won't
  extend `OutputType_Projection` (fixed?)
- [ ] global named projections should be visible between files (it is somewhat visible now, only from the same namespace)
  - projections should be importable
- [ ] Make op projections merge tags from entity tails. Having an op projection like `:rec(a) :~ Bar:rec(b)` should
  allow request projection like `:rec(a) ~ BRec(b)` (if `Bar:rec` type is `BarRec`). Procedure should be:
  - for every entity tail: for every tag: if this tag is present in the main projection
  - if tag type is the same: merge tag projections (? or ignore?)
  - else add it as main projection tag's tail
- [x] BIG: refactor projections after `Type`/`DataType`/`DatumType` hierarchy: self-var projections should NOT
  be represented by entity-model projections pair, this leads to messy code
- [ ] op output parser: check if 'include in default' fields have required parameters without defaults
- [ ] BIG move flag from entity back projections to fields/collection items. Because:
  ```
  outputProjection user: User = :(
    rec (
      bestFriend $user
      +worstEnemy $user
    )
  )
  ```
  By the same reason flag should be moved from models to tags.
  Think about how this flag is going to be passed around. Projection + flag in the signatures?
- [ ] req projections codegen: try to give better names to ((built-in?) primitive?) field projections, i.e.
  ```
  .firstName.OutputStringProjection => .firstName.OutputFirstNameProjection
  ```
- [ ] req projections: flag should be correctly applied to defaults. `(+bestFriend)` => `(+bestFriend:+id)`
- [ ] references stuff is messy: resolution order, namespaces, normalized projections, transformations. Must be redesigned.
  - `foo ( bar $bar )` doesn't decide on `$bar` namespace (inside current projection or more global), yet currently `$bar` reference
     gets created in the current (innermost) namespace and later resolved in the corrent namespace (hopefully)
  - ` ( foo ) ~Bar $bar = ( bar )` must result in `$bar` created in the same NS as current projection. This is hard for 2 reasons:
     first, a reference to `$bar` may have already been created (see above); and second, this reference must involve a call
     to `normalize` once current projection is resolved. This plays badly with projection transformers which may replace
     current projection with something else.

# Operations

# Service

# Build
- Gradle
- Maven

# Cleanup
  - [ ] check out artifactory-tools to clean up old light-psi versions
    - see `ArtifactoryClient`
  - [ ] DataType(Api): type should be EntityType ?
  - [ ] Rename Data/Var/Union type to some common name

# Docs
- [ ] assemblers: update library example docs

# Reflection service
- [ ] annotations support

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

# Instrument maven plugin(s) with jacoco agent?
- [ ] e.g.: argLine set to -javaagent:/home/travis/.m2/repository/org/jacoco/org.jacoco.agent/0.7.9/org.jacoco.agent-0.7.9-runtime.jar=destfile=/home/travis/build/SumoLogic/epigraph/target/jacoco.exec
- [ ] then add the .exec to considered ones

# Move java codegen classes into a subpackage

# Various bugs
 - [ ] [konst-frontend-1]:konstantin@~$ g 'http://localhost:23662/som/files/100025(id,name)~FolderItem(...)'
       Internal error 'java.lang.IllegalArgumentException: Tail type 'com.sumologic.som.FolderItem' is assignable from model type 'com.sumologic.som.FolderItem'. Tail defined at <unknown> line 1 (offset range 33-38)'

# Java 10
 - [ ] Generate `module-info.java` for generated modules (needed for, e.g., `@javax.annotations.Generated`)
