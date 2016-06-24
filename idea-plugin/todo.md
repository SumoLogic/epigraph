### High
- [ ] Data language must be injected after all. Otherwise we will have at least two different instances of it (for schema attributes and for stencil parameters) and will have to support them separately in the plugin: references, refactorings etc

### Medium
- [ ] Type ref completion should respect kinds (e.g. `record Foo extends <caret>` should only suggests records, with current one and all the parents removed)
- [ ] Structure popup (ctrl-f12)
- [ ] Rename on fields (**In progress**)
- [ ] Rename on vartype tags

### Low
- [ ] Type ref completion should suggest types from non-imported namespaces, automatically adding imports as needed
- [ ] Custom attributes must reference fields in meta-schema (and respect renames, find usages etc)
- [ ] Custom attribute values must be based (and validated against) meta-schema. Find usages/refactorings/completion must take it into account
