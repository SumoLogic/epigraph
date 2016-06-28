### High
- [x] `record A; record B extends A` rename A -- get a broken ref in `extends`

### Medium
- [ ] Ambiguous ref not highlighted:

        // file1: namespace some; string String
        // file2: namespace some; string Foo extends String   <-- either epigraph.String or some.String

- [ ] Enquoted var tag refs not recognized:

        vartype Foo default `string` {
          string: String
        }

### Low
- [ ] Goto type: namespaces in the drop-down list should be in the parens
- [ ] Wrong parent type not checked in `supplement`