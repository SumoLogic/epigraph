## Project Features and Roadmap

### 1. Type system

#### 1.1. Data types:
  * [x] Records
  * [x] ~~Unions~~
  * [ ] Maps
  * [x] Lists
  * [ ] Enums
    + [ ] Typed data associated with enum constants? (e.g. `Color.Red.value.wavelength`)
  * [x] Primitives:
    * [x] Strings
    * [ ] Integers
    * [ ] Longs
    * [ ] Doubles
    * [ ] Booleans

#### 1.2. Var types
  * [ ] Explicitly defined var types
  * [ ] Implicit default var types (for declared or inferred data types)

#### 1.3. Polymorphic types
  Normally data contract is defined by declared data type of the type member of the var holding
  the data. In some cases it's desirable to know actual type of the datum - in these cases declared
  data type should be marked as `polymorphic`.

#### 1.4. Namespaces

#### 1.5. Type inheritance
  * [ ] For data types
  * [ ] For var types

#### 1.6. Type supplements (augmenting existing types)
  * [ ] Adding supertypes to existing data types
  * [ ] Adding supertypes to existing var types

#### 1.7. Structured data templates
  * [ ] Request templates
  * [ ] Input data templates
  * [ ] Output templates
  * [ ] Parameters
    * [ ] On fields / tags
    * [ ] On data types

#### 1.8. Metadata

### 2. Schema language
  * [ ] Human-authorable
  * [ ] Computer-readable
  * [ ] Extensible with custom attributes:
    + [ ] Attribute data language
    + [ ] Data validation (for known attribute types)
    + [ ] Data well-formedness (for attributes of unknown types)
  * Data type modifiers:
    + [ ] `polymorphic` (actual data type of the datum will be available at runtime)
    + [ ] `abstract` (the data type is intended for extension and should not be instantiatable on its own)
  * Field modifiers:
    + [ ] `abstract` (non-abstract subtypes must provide their own (refined) declaration of the field)
    + [ ] `override` (the field must be already declared in the supertype(s))
  * Var type member modifiers:
    + [ ] `abstract`? (subtypes must provide their own (refined) declaration of the member)
    + [ ] `override` (var type member must be already known in the supertype(s))
  * [ ] Naming conventions for:
    * [ ] Namespaces
    * [ ] Types
    * [ ] Anonymous List types (`list[org.example.Bar]`? `listOf.org.example.Bar`?)
    * [ ] Anonymous Map types (`map[org.example.Foo, org.example.Bar default baz]`?)
    * [ ] Fields ~~and Tags~~
    * [ ] Enum values (`ALLCAPS`? `UpperCamelCase`?)
    * [ ] Type members

### 3. Schema authoring plugin(s)
  * [ ] IntelliJ IDEA Plugin
    * [x] Schema syntax highlighting and validation
    * [ ] Embedded data language highlighting and validation
    * [ ] Refactoring
    * [x] Finding references
    * [ ] Semantic checks
    * [ ] Type hierarchy support
    * [ ] Support for sub/super gutter icons and navigation

### 4. Schema compiler
  * [ ] Maven plugin
  * [ ] IDEA plugin?

### 5. Code generation
  * [ ] Maven plugin
  * [ ] IDEA plugin

### 6. Scala bindings
  * [ ] Types
  * [ ] Immutable data
  * [ ] Mutable data
  * [ ] Data builders

### 7. HTTP Service container
  * [ ] Asynchronous
  * [ ] Wire protocols:
    * [ ] JSON(s)
    * [ ] Binary


### 8. Permissions
### 9. IDL
