/* Created by yegor on 10/8/16. */

package ws.epigraph.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class JavaNames {

  /**
   * Strings that are not allowed as legal Java identifiers.
   * (https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.8)
   */
  public static final Set<? extends String> reserved = Unmodifiable.set(new LinkedHashSet<>(Arrays.asList(

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

  )));

  public static @NotNull String jn(@NotNull String n) { return reserved.contains(n) ? n + '_' : n; }

}
