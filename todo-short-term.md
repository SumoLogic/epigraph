- [x] remove `requried` from req output field projections. Figure out how to add it to tag-less models
  - [x] '+' on field must also mark all models in poly tails as required, recursively
- [x] remove field parameters/annotations
  - [x] req projections
  - [x] op projections
- [x] check if `update` on `ReqUpdateFieldProjection` is OK (syntax, semantics)
- [x] generate req paths
- [ ] undertow handler: remove trimmer, should be handled by marshaller

- [ ] remove type information from projections?
  - [ ] record projections should contain a String->FP, not String->FPE map

- [ ] fix projections pretty printer for records, see OpOutputProjectionsTest.tetParsing
- [ ] generated req projections: cache normalized projections

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
