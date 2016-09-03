/* Created by yegor on 9/1/16. */

package io.epigraph.names;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataTypeName {

  public final boolean polymorphic;

  public final @NotNull TypeName typeName;

  public final @Nullable String defaultTagName;

  public DataTypeName(boolean polymorphic, @NotNull TypeName typeName, @Nullable String defaultTagName) {
    this.polymorphic = polymorphic;
    this.typeName = typeName;
    this.defaultTagName = defaultTagName;
  }

  public static @NotNull DataTypeName get(boolean polymorphic, @NotNull TypeName typeName, @Nullable String defaultTagName) {
    return new DataTypeName(polymorphic, typeName, defaultTagName); // TODO cache?
  }

  @Override
  public String toString() { // TODO cache?
    return (polymorphic ? "polymorphic " : "") + typeName + (defaultTagName == null ? "" : "default" + defaultTagName);
  }

}
