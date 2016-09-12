### High
- `//` and `/*... */` comments should be ignored in string literals (seems to be fixed already?)

### Medium
- `record Foo extends Foo`, now erase `extends Foo` -- circular inheritance error stays
- `polymorphic` completion inside anon lists and maps
- renaming a tag should suggest to rename base/sub tags (like renaming a field does)
- renaming a field/tag with `override` modifier should rename base members too (and sub-members with `override`) 
- inline hints for default tags don't always get updated. Move their logic out of SchemaAnnotator to a separate editor listener component

### Low
- String text should be highlihted as specified in "Preferences > Editor > Colors & Fonts > Language Defaults > String > String Text"
- Detect useless `extends` e.g. `record Foo extends Bar, Bar`. Take hierarchy into account
- Ctrl-space marks file as dirty
- Wrong parent type not checked in `supplement`
- would be nice to teach idea to understand src/main|test/epigraph as sources|testsources root automagically (done for gradle, add to sources autodetection during project import)
- Code completion for `extend`, `supplements`, `supplements` must understand collections well: `List[Foo]` can extend `List[Bar]` only if `Foo` extends `Bar`

----
## Done
### High
- Field/Tag completion after `default` in records/vartypes: currently suggests `namespace`.. should suggest all the super fields/tags
- Var tag references not resolving in `default` overrides on fields/list elements etc
- tooltip on unresolved tags

### Medium
- Schema files under `resources` should be excluded from analysis
- completion for `default` keyword doesn't always work, see CompletionTest::testOverrideCompletionInRecord
- inline hints for default tags don't always get updated. Move their logic out of SchemaAnnotator to a separate editor listener component

### Low
- Icon is not substituted for schema files outside of source root, in the files view. See https://intellij-support.jetbrains.com/hc/en-us/community/posts/207277349-handling-custom-language-file-outside-of-source-root
