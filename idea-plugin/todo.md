### High

### Medium
- Schema files must only be fully parsed if under (test) source root
- Module dependencies must be respected when resolving references. Tricky for injections: only modules that depend on the current one must be checked
- Structure popup (ctrl-f12)
- Rename on fields (**In progress**)
- Rename on vartype tags

### Low
- Rename projects to `epigraph-idea-plugin` and `epigraph-schema-parser(-common)` ?
- Type ref completion should suggest types from non-imported namespaces, automatically adding imports as needed
- Custom attributes must reference fields in meta-schema (and respect renames, find usages etc)
- Custom attribute values must be based (and validated against) meta-schema. Find usages/refactorings/completion must take it into account

---
## Done
### High
- Remove context-aware lexing, a set of keywords must not change depending on the context

### Medium

### Low
