- [ ] simple service quick-start guide

- [ ] support projection snippets in schema. Lots of code duplication otherwise!
  - [ ] change schema grammar
  - [ ] attach names (FQNs?) to (op?) var projections
  - [ ] make var projections half-mutable? find a way to have var projection references/resolve them
  - [ ] introduce context to op psi parsers, use it to store (potentially unfinished) named var projections
  - [ ] update psi parsers
  - [ ] update op codegen
  - [ ] update req codegen: req projections must be reusable too


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
