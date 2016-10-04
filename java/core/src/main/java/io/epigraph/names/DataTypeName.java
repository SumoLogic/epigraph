/* Created by yegor on 9/1/16. */

package io.epigraph.names;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class DataTypeName {

  public final @NotNull TypeName typeName;

  public final @Nullable String defaultTagName;

  public DataTypeName(@NotNull TypeName typeName, @Nullable String defaultTagName) {
    this.typeName = typeName;
    this.defaultTagName = defaultTagName;
  }

  public static @NotNull DataTypeName get(@NotNull TypeName typeName, @Nullable String defaultTagName) {
    return new DataTypeName(typeName, defaultTagName); // TODO cache somewhere?
  }

  @Override // TODO cache?
  public String toString() { return typeName + (defaultTagName == null ? "" : " default " + defaultTagName); }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DataTypeName that = (DataTypeName) o;
    return Objects.equals(typeName, that.typeName) && Objects.equals(defaultTagName, that.defaultTagName);
  }

  @Override
  public int hashCode() { return Objects.hash(typeName, defaultTagName); } // TODO cache?

}
