### High
- Gutter icons are broken

### Medium
- Ambiguous ref not highlighted:

        // file1: namespace some; string String
        // file2: namespace some; string Foo extends String   <-- either epigraph.String or some.String
- Type ref completion should respect kinds, e.g. `record Foo extends <caret>` should only suggests records, with current one and all the parents removed

### Low
- Wrong parent type not checked in `supplement`

----
## Done
### High
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
