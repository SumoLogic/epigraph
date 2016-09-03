/* Created by yegor on 9/1/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.names.DataTypeName;
import io.epigraph.types.Type.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Declares type of data held by container type components (record fields, list elements, map values).
 */
public class DataType {

  public final boolean polymorphic;

  public final @NotNull Type type;

  public final @Nullable Tag defaultTag;

  public final @NotNull DataTypeName name;

  public DataType(boolean polymorphic, @NotNull Type type, @Nullable Tag defaultTag) {
    this.polymorphic = polymorphic;
    this.type = type;
    this.defaultTag = defaultTag;
    this.name = new DataTypeName(polymorphic, type.name(), defaultTag == null ? null : defaultTag.name);
  }



//  public boolean polymorphic() { return polymorphic; }
//
//  public @NotNull Type type() { return type; }
//
//  public @Nullable Tag defaultTag() { return defaultTag; }

  public <D extends Data> D checkWrite(@NotNull D data) throws IllegalArgumentException {
    if (polymorphic) {
      if (!type.isAssignableFrom(data.type())) throw new IllegalArgumentException();
    } else {
      if (type != data.type()) throw new IllegalArgumentException();
    }
    return data;
  }

}
