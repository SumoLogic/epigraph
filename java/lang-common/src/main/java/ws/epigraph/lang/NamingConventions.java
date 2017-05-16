/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.lang;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class NamingConventions {
  private NamingConventions() {}

  // TODO validation logic should be common with epigraph-java|scala-core?

  public static @Nullable String validateTypeName(@NotNull String typeName) {
    if (typeName.isEmpty()) return null;
    if (isProperlyQuoted(typeName)) return null;

    if (!Character.isUpperCase(typeName.charAt(0)))
      return "Type name must start with an upper case letter";

    return null;
  }

  public static @Nullable String validateFieldName(@NotNull String fieldName) {
    if (isProperlyQuoted(fieldName)) return null;

    return validateMemberName(fieldName, "Field name");
  }

  public static @Nullable String validateVarTypeTagName(@NotNull String varTagName) {
    if (isProperlyQuoted(varTagName)) return null;

    return validateMemberName(varTagName, "VarType tag");
  }

  private static @Nullable String validateMemberName(@NotNull String memberName, @NotNull String memberKind) {
    if (memberName.isEmpty()) return null;
    if (isProperlyQuoted(memberName)) return null;

    if (!Character.isLowerCase(memberName.charAt(0)))
      return memberKind + " must start with a lower case letter";

    return null;
  }

  @Contract(pure = true)
  public static boolean isProperlyQuoted(@NotNull String name) {
    return name.length() > 1 && name.startsWith("`") && name.endsWith("`");
  }

  @Contract(pure = true)
  public static @NotNull String enquote(@NotNull String name) {
    return '`' + name + '`';
  }

  @Contract(pure = true)
  public static @NotNull String unquote(@NotNull String name) {
    if (isProperlyQuoted(name)) return name.substring(1, name.length() - 1);
    return name;
  }
}
