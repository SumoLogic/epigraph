This module contains tests that can't be put in their respective modules to avoid circular dependencies.

For instance a lot of tests depend on projection PSI parsers, but belong to modules that are requried
by PSI parser implementations.