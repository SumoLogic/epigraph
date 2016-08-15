### High

### Medium
- namespace auto-completion suggests TypeNames in addition to package names
- Ambiguous ref not highlighted:

        // file1: namespace some; string String
        // file2: namespace some; string Foo extends String   <-- either epigraph.String or some.String
- Type ref completion should respect kinds, e.g. `record Foo extends <caret>` should only suggests records, with current one and all the parents removed
- `java.lang.AssertionError: Stub list in names.esc has more elements than PSI`, not reproducible so far
- renaming a tag should suggest to rename base/sub tags (like renaming a field does)
- renaming a field/tag with `override` modifier should rename base members too (and sub-members with `override`) 

### Low
- Ctrl-space marks file as dirty
- Icon is not substituted for schema files outside of source root, in the files view. See https://intellij-support.jetbrains.com/hc/en-us/community/posts/207277349-handling-custom-language-file-outside-of-source-root
- Wrong parent type not checked in `supplement`
- would be nice to teach idea to understand src/main|test/epigraph as sources|testsources root automagically
- extends auto-complete should suggest compatible (kind-wise) types instead of all known ones

----
## Done
### High

### Medium
- `quoted` field/tag names not recognized as overriding same fields/tags (quoted or not) from parent type

### Low
