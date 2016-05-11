package com.sumologic.epigraph.ideaplugin.schema.brains;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class NamingConventions {
  // TODO this should belong elsewhere

  @Nullable
  public static String validateTypeName(@NotNull String typeName) {
    if (!Character.isUpperCase(typeName.charAt(0))) // TODO account for _
      return "Type name must start with an upper case letter";

    return null;
  }

  @Nullable
  public static String validateFieldName(@NotNull String fieldName) {
    return validateMemberName(fieldName, "Field name");
  }

  @Nullable
  public static String validateTagName(@NotNull String tagName) {
    return validateMemberName(tagName, "Tag name");
  }

  @Nullable
  public static String validateVarTypeMemberName(@NotNull String varTypeMemberName) {
    return validateMemberName(varTypeMemberName, "VarType type alias");
  }

  @Nullable
  private static String validateMemberName(@NotNull String fieldName, @NotNull String memberKind) {
    if (!Character.isLowerCase(fieldName.charAt(0)))
      return memberKind + " must start with a lower case letter";

    return null;
  }
}
