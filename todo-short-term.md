- [ ] paths: ~~add entity params,~~ make tags optional (so it's possible to have path params without anything else, i.e. without having to change operation type)
  - alternatively put params on input projections, but introduce defaults so that there's no need
    to spell out the whole projection. op input projections should be fully converted into defaults,
    op output should use flags to mark default parts
- [ ] bug: `(a, b) ~Foo(c) ~Bar $bar = (d)` => `$bar` will include (d,c) but not (a,b)
- [ ] bug? `(+foo)` if foo is an entity type without retro tag, `+` seems to have no effect
- [ ] op parameter projections should have their own reference context, with global/resource input context as a parent
- [ ] `UriComposer`: make sure `+` is added before flagged delete entity projections (+UT)
- [ ] reverse the meaning of `+` (required) on OpInput and ReqOutput projections
- [x] sort out 'path steps' for input projections: input data should respect them
- [x] codegen: projection parameter getters should only unwrap built-in primitives (but not, say, `UserId`)
- [x] codegen: `_resources/*` package name should be in lower case
- [x] codegen: primitive `String` setters should accept `CharSequence`
- [x] get rid of `<` before input projections in URLs
- [ ] do something about nullable/optional in schema/projections
  - make generated records throw errors if asked for non-existing fields?
  - then add `@NotNull` on fields in schema, translated into `@NotNull` on generated records
  - https://sumologic.slack.com/archives/D0JPD1FKN/p1507142820000118
- [ ] server based on spring http handler

- Federator
  - [ ] allow paths to end with model types (prereq. for fed. ?)
  - [x] support named projections on the top level (not inside service)
  - [x] add transformers to schema
    - [x] codegen implementation stubs
  - [ ] operations -> transformers via annotations

  - [ ] `toBuilder` on data? Will be tricky because of covariance
  - [ ] add `api` section to schema, listing a set of exposed resources and if they're federated
    - [ ] codegen something from it
    - [ ] validate at startup that stuff is actually?

# Yegor dependencies
- [ ] add enums

# See also
- See [General todo] (todo.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
