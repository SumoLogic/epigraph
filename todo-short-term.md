- [x] support projection snippets in schema. Lots of code duplication otherwise!
  - [x] change schema grammar
  - [x] attach names (FQNs?) to (op?) var projections
  - [x] make var projections half-mutable? find a way to have var projection references/resolve them
  - [x] introduce context to op psi parsers, use it to store (potentially unfinished) named var projections
  - [x] update psi parsers
  - [x] update op codegen
  - [x] update req codegen: req projections must be reusable too
  - [x] req projections syntax
  - [x] req projections psi parsers
  - [x] allow recursive data: update marshaller/unmarshaller
    - [x] should check that recursive data corresponds to projection

- [x] undertow handler: remove trimmer, should be handled by marshaller

- Federator prerequisites
  - [ ] `toBuilder` on data
  - [ ] remote service invocation layer
  - [ ] introduce transformers +codegen?

- Docs
  - [ ] service quick-start guide
    - [x] simple read
    - [ ] search operation
  - Operations
    - [ ] Overview
    - [ ] IDL
    - [ ] Routing

# See also
- See [General todo] (todo.md)
- See [IDEA plugin todo](idea-plugin/todo.md)
- See [IDEA plugin bugs](idea-plugin/bugs.md)
- See `**/issues.md`
