/* Created by yegor on 8/3/16. */

package io.epigraph.data;

import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface Datum {

  @NotNull DatumType type();

  static @Nullable Datum.Imm toImmutable(@Nullable Datum datum) { return datum == null ? null : datum.toImmutable(); }

  @NotNull Datum.Raw _raw();

  @NotNull Datum.Imm toImmutable();


  abstract class Impl<MyType extends DatumType> implements Datum {

    private final @NotNull MyType type;

    protected Impl(@NotNull MyType type) { this.type = type; }

    @Override
    public final @NotNull MyType type() { return type; }

  }


  interface Raw extends Datum {

    @Override
    @NotNull Datum.Imm.Raw toImmutable();

  }


  interface Static extends Datum {

    @Override
    @NotNull Datum.Imm.Static toImmutable();

  }


  interface Imm extends Datum, Immutable {

    interface Raw extends Datum.Imm, Datum.Raw {}

    interface Static extends Datum.Imm, Datum.Static {}

  }


  interface Mut extends Datum, Mutable {

    interface Raw extends Datum.Mut, Datum.Raw {}

    interface Static extends Datum.Mut, Datum.Static {}

  }


}
