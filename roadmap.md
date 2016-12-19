## Project Features and Roadmap

### 1. Type system

#### 1.1. Data types:
  * [x] Records
  * [ ] ~~Unions~~ [replaced with polymorphic projections]
  * [x] Maps
  * [x] Lists
  * [ ] Enums
    + [ ] Typed data associated with enum constants? (e.g. `Color.Red.value.wavelength`)
  * [x] Primitives:
    * [x] Strings
    * [x] Integers
    * [x] Longs
    * [x] Doubles
    * [x] Booleans
    * [ ] Bytes / binary?

#### 1.2. Var types
  * [x] Explicitly defined var types
  * [x] Implicit default var types (for declared or inferred data types)
  * [x] Default ~~var~~ data type members
    * [ ] Rename `default` keyword to `retro` (to highlight the backwards-compatibility purpose of these)?

#### 1.3. ~~Polymorphic types~~ [replaced with polymorphic projections]
  ~~Normally data contract is defined by declared data type of the type member of the var holding
  the data. In some cases it's desirable to know actual type of the datum - in these cases declared
  data type should be marked as `polymorphic`.~~

#### 1.4. Namespaces

#### 1.5. Type inheritance
  * [x] For data types
  * [x] For var types

#### 1.6. Type supplements (augmenting existing types)
  * [x] Adding supertypes to existing data types
  * [x] Adding supertypes to existing var types

#### 1.7. Structured data templates (projections)
  * [x] Request templates
  * [x] Input data templates
  * [x] Output templates
  * [x] Parameters
    * [x] On fields / tags
    * [x] On data types

#### 1.8. Metadata

### 2. Schema language
  * [x] Human-authorable
  * [x] Computer-readable
  * [ ] Extensible with custom attributes:
    + [ ] Attribute data language
    + [ ] Data validation (for known attribute types)
    + [ ] Data well-formedness (for attributes of unknown types)
  * Data type modifiers:
    + [ ] ~~`polymorphic` (actual data type of the datum will be available at runtime)~~
    + [ ] `abstract` (the data type is intended for extension and should not be instantiatable on its own)
  * Field modifiers:
    + [ ] `abstract` (non-abstract subtypes must provide their own (refined) declaration of the field)
    + [x] `override` (the field must be already declared in the supertype(s))
      + [ ] compiler checks
  * Var type member modifiers:
    + [ ] `abstract`? (subtypes must provide their own (refined) declaration of the member)
    + [ ] `override` (var type member must be already known in the supertype(s))
  * [ ] Naming conventions for:
    * [x] Namespaces
    * [x] Types
    * [x] Anonymous List types (`list[org.example.Bar]` ~~`listOf.org.example.Bar`?~~)
    * [x] Anonymous Map types (`map[org.example.Foo, org.example.Bar default baz]`)
    * [x] Fields ~~and Tags~~
    * [ ] Enum values (`ALLCAPS`? `UpperCamelCase`?)
    * [x] Type members (tags)

### 3. Schema authoring plugin(s)
  * [ ] File extension (.epigraph - EpigraphSChema? .epigraph?)
  * [x] IntelliJ IDEA Plugin
    * [x] Schema syntax highlighting and validation
    * [x] Embedded data language highlighting and validation
    * [ ] Refactoring
    * [x] Finding references
    * [ ] Semantic checks
    * [x] Type hierarchy support
    * [x] Support for sub/super gutter icons and navigation

### 4. Schema compiler
  * [x] Maven plugin
  * [x] Gradle plugin
  * [ ] IDEA plugin?

### 5. Schema-driven code generation
  * [x] Maven plugin
    * [x] Java
      * [ ] Staticly-typed projections 
    * [ ] JavaScript
    * [ ] Scala?
  * [ ] Gradle plugin
  * [ ] IDEA plugin

### 6. Java bindings
  * [x] Types
  * [x] Immutable data
  * [ ] ~~Mutable data~~
  * [x] Data builders

### 7. Scala bindings?
  * [ ] Types
  * [ ] Immutable data
  * [ ] Mutable data
  * [ ] Data builders

### 8.

### 9. IDL (Merged with Schema)
  * [ ] ~~File extension (.sdl - Service Description Language? .scl - Service Contract Language?)~~
  * [x] Resources
  * [x] Operations
    * [x] Read
    * [x] Create
    * [x] Update
    * [x] Delete
    * [x] Custom actions

### 10. Schema-driven code generation
  * [ ] Serivice stubs
  * [ ] Resource stubs
  * [ ] Operation interfaces

### 11. HTTP Service
  * [x] Undertow
  * [ ] Servlet
    * [ ] Async Servlet

### 12. Wire/serialization formats
  * [x] JSON
    * [x] Streaming Writer
    * [x] Streaming Reader
  * [ ] MessagePack?
  * [ ] Avro?
  * [ ] Custom binary

### 13. Federation / composition / orchestration library


### 14. Service client bindings
  * [ ] Java
    * [ ] Generic request builders, response parsers
    * [ ] Service/resource/operation specific generated code
