package com.sumologic.epigraph.schema.parser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class NamingConventions {
  // TODO this should belong elsewhere

  @Nullable
  public static String validateTypeName(@NotNull String typeName) {
    if (isProperlyQuoted(typeName)) return null;

    if (!Character.isUpperCase(typeName.charAt(0)))
      return "Type name must start with an upper case letter";

    return null;
  }

  @Nullable
  public static String validateFieldName(@NotNull String fieldName) {
    if (isProperlyQuoted(fieldName)) return null;

    return validateMemberName(fieldName, "Field name");
  }

  @Nullable
  public static String validateTagName(@NotNull String tagName) {
    if (isProperlyQuoted(tagName)) return null;

    return validateMemberName(tagName, "Tag name");
  }

  @Nullable
  public static String validateVarTypeMemberName(@NotNull String varTagName) {
    if (isProperlyQuoted(varTagName)) return null;

    return validateMemberName(varTagName, "VarType type alias");
  }

  @Nullable
  private static String validateMemberName(@NotNull String fieldName, @NotNull String memberKind) {
    if (isProperlyQuoted(fieldName)) return null;

    if (!Character.isLowerCase(fieldName.charAt(0)))
      return memberKind + " must start with a lower case letter";

    return null;
  }

  @Contract(pure = true)
  public static boolean isProperlyQuoted(@NotNull String name) {
    return name.length() > 1 && name.startsWith("`") && name.endsWith("`");
  }

  @Contract(pure = true)
  @NotNull
  public static String enquote(@NotNull String name) {
    return '`' + name + '`';
  }

  @Contract(pure = true)
  @NotNull
  public static String unquote(@NotNull String name) {
    if (isProperlyQuoted(name)) return name.substring(1, name.length() - 1);
    return name;
  }
}
