- [ ] simple service quick-start guide

- [ ] support projection snippets in schema. Lots of code duplication otherwise!
  - [x] change schema grammar
  - [x] attach names (FQNs?) to (op?) var projections
  - [x] make var projections half-mutable? find a way to have var projection references/resolve them
  - [x] introduce context to op psi parsers, use it to store (potentially unfinished) named var projections
  - [x] update psi parsers
  - [x] update op codegen
  - [x] update req codegen: req projections must be reusable too
  - [ ] req projections syntx
  - [ ] req projections psi parsers
  - [ ] allow recursive data: update marshaller/unmarshaller


- [ ] undertow handler: remove trimmer, should be handled by marshaller

- Federator prerequisites
  - [ ] `toBuilder` on data
  - [ ] remote service invocation layer
  - [ ] introduce transformers +codegen?

- Docs
  - Operations
    - [ ] Overview
    - [ ] IDL
    - [ ] Routing

# See also
- See [General todo] (todo.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
