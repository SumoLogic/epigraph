/* Created by yegor on 9/1/16. */

package ws.epigraph.types;

import ws.epigraph.data.Data;
import ws.epigraph.names.DataTypeName;
import ws.epigraph.types.Type.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Declares type of data held by container type components (record fields, list elements, map values).
 */
public final class DataType {

  public final @NotNull Type type;

  public final @Nullable Tag defaultTag;

  public final @NotNull DataTypeName name;

  public DataType(@NotNull Type type, @Nullable Tag defaultTag) {
    this.type = type;
    this.defaultTag = defaultTag;
    this.name = new DataTypeName(type.name(), defaultTag == null ? null : defaultTag.name);
  }

//  public @NotNull Type type() { return type; }
//
//  public @Nullable Tag defaultTag() { return defaultTag; }

  public <D extends Data> D checkAssignable(@NotNull D data) throws IllegalArgumentException {
    return type.checkAssignable(data);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DataType dataType = (DataType) o;
    return Objects.equals(name, dataType.name);
  }

  @Override
  public int hashCode() { return name.hashCode(); }

}
