package io.epigraph.schema.parser.psi;

public enum PrimitiveTypeKind {
  STRING("string"),
  INTEGER("integer"),
  LONG("long"),
  DOUBLE("double"),
  BOOLEAN("boolean");

  public final String name;

  PrimitiveTypeKind(String name) {
    this.name = name;
  }
}
