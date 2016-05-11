## Project Features and Roadmap

### 1. Type system

#### 1.1. Data types:
  * Records
  * Unions
  * Maps
  * Lists
  * Enums
  * Primitives:
    * Strings
    * Integers
    * Longs
    * Doubles
    * Booleans

#### 1.2. Var types
  * Explicitly defined var types
  * Implicit default var types (for declared or inferred data types)

#### 1.3. Namespaces

#### 1.4. Type inheritance
  * For data types
  * For var types

#### 1.5. Type supplements (augmenting existing types)
  * Adding supertypes to existing data types
  * Adding supertypes to existing var types

#### 1.6. Structured data templates
  * Request templates
  * Input data templates
  * Output templates
  * Parameters
    * On fields / tags
    * On data types

#### 1.7. Metadata

### 2. Schema language
  * Human-authorable
  * Computer-readable
  * Extensible with custom attributes:
    + Attribute data language
    + Data validation (for known attribute types)
    + Data well-formedness (for attributes of unknown types)
  * Naming conventions for:
    * Namespaces
    * Types
    * Anonymous List and Map types
    * Fields and Tags
    * Enum values
    * Type members

### 3. Schema authoring plugin(s)
  * IntelliJ IDEA Plugin
    * Schema syntax highlighting and validation
    * Embedded data language highlighting and validation
    * Refactoring
    * Finding references
    * Semantic checks

### 4. Schema compiler
  * Maven plugin
  * IDEA plugin?

### 5. Code generation
  * Maven plugin
  * IDEA plugin

### 6. Scala bindings
  * Types
  * Immutable data
  * Mutable data
  * Data builders

### 7. HTTP Service container
  * Asynchronous
  * Wire protocols:
    * JSON(s)
    * Binary
