/* Created by yegor on 8/15/16. */

package com.sumologic.epigraph.java

object JavaReserved {

  /**
   * Strings that are not allowed as legal Java identifiers.
   * (https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.8)
   */
  val reserved: Set[String] = Set(
    // keywords (https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.9)
    "abstract",
    "continue",
    "for",
    "new",
    "switch",
    "assert",
    "default",
    "if",
    "package",
    "synchronized",
    "boolean",
    "do",
    "goto",
    "private",
    "this",
    "break",
    "double",
    "implements",
    "protected",
    "throw",
    "byte",
    "else",
    "import",
    "public",
    "throws",
    "case",
    "enum",
    "instanceof",
    "return",
    "transient",
    "catch",
    "extends",
    "int",
    "short",
    "try",
    "char",
    "final",
    "interface",
    "static",
    "void",
    "class",
    "finally",
    "long",
    "strictfp",
    "volatile",
    "const",
    "float",
    "native",
    "super",
    "while",
    // null literal (https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.10.7)
    "null",
    // boolean literals (https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.10.3)
    "true",
    "false"
  )

}
