/* Created by yegor on 9/1/16. */

package io.epigraph.names;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
    return (polymorphic ? "polymorphic " : "") + typeName + (defaultTagName == null ? "" : " default " + defaultTagName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DataTypeName that = (DataTypeName) o;
    return polymorphic == that.polymorphic &&
           Objects.equals(typeName, that.typeName) &&
           Objects.equals(defaultTagName, that.defaultTagName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(polymorphic, typeName, defaultTagName);
  }
}
