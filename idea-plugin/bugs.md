### High
- [x] `record A; record B extends A` rename A -- get a broken ref in `extends`

### Medium
- [ ] Ambiguous ref not highlighted:

        // file1: namespace some; string String
        // file2: namespace some; string Foo extends String   <-- either epigraph.String or some.String
