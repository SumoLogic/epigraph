### High

### Medium
- Ambiguous ref not highlighted:

        // file1: namespace some; string String
        // file2: namespace some; string Foo extends String   <-- either epigraph.String or some.String
- Type ref completion should respect kinds, e.g. `record Foo extends <caret>` should only suggests records, with current one and all the parents removed

### Low
- Wrong parent type not checked in `supplement`
- namespace on new file not inferred from directory structure (when under src/main|test/epigraph/...)
- namespace auto-completion not working (when under src/main|test/epigraph/...)
- would be nice to teach idea to understand src/main|test/epigraph as sources|testsources root

----
## Done
### High
- Gutter icons are broken
- `record A; record B extends A` rename A -- get a broken ref in `extends`

### Medium
- `Find Usages` doesn't work on var tags (with `abstract` modifier)
- Enquoted var tags get improperly highlighted
- Enquoted var tag refs not recognized:

        vartype Foo default `string` {
          string: String
        }

### Low
- Goto type: namespaces in the drop-down list should be in the parens
