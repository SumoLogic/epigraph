/* Created by yegor on 7/25/16. */

package io.epigraph.data;

import io.epigraph.data.immutable.ImmData;
import io.epigraph.errors.ErrorValue;
import io.epigraph.types.Type;
import io.epigraph.types.Type.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Collection of tagged values representing the same "entity".
 */
public interface Data { // TODO Var? Union? Values?

  public @NotNull Type type();

  public @NotNull Map<@NotNull String, @NotNull ? extends Value> values();

  public @Nullable Datum getDatum(@NotNull Tag tag);

  public @Nullable ErrorValue getError(@NotNull Tag tag);

  public @Nullable Value getValue(Tag tag);

  public @NotNull ImmData toImmutable();


}
