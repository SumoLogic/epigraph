### High

### Medium
- Ambiguous ref not highlighted:

        // file1: namespace some; string String
        // file2: namespace some; string Foo extends String   <-- either epigraph.String or some.String
- Type ref completion should respect kinds, e.g. `record Foo extends <caret>` should only suggests records, with current one and all the parents removed

### Low
- Icon is not substituted for schema files outside of source root, in the files view. See https://intellij-support.jetbrains.com/hc/en-us/community/posts/207277349-handling-custom-language-file-outside-of-source-root
- Wrong parent type not checked in `supplement`
- namespace auto-completion suggests TypeNames in addition to package names
- would be nice to teach idea to understand src/main|test/epigraph as sources|testsources root automagically

----
## Done
### High

### Medium

### Low
