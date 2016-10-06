### High
- `//` and `/*... */` comments should be ignored in string literals (seems to be fixed already?)

### Medium
- `record Foo extends Foo`, now erase `extends Foo` -- circular inheritance error stays
- renaming a tag should suggest to rename base/sub tags (like renaming a field does)
- renaming a field/tag with `override` modifier should rename base members too (and sub-members with `override`)
- namespaces in UpperCamelCase should trigger errors
- Parser error reporting often points to spaces before bad token instead of the token itself:
        
        map [ 33
           ^ error points to this space instead of '33'

### Low
- Detect useless `extends` e.g. `record Foo extends Bar, Bar`. Take hierarchy into account
- Ctrl-space marks file as dirty
- Wrong parent type not checked in `supplement`
- would be nice to teach idea to understand src/main|test/epigraph as sources|testsources root automagically (done for gradle, add to sources autodetection during project import)
- Code completion for `extend`, `supplements`, `supplements` must understand collections well: `List[Foo]` can extend `List[Bar]` only if `Foo` extends `Bar`

