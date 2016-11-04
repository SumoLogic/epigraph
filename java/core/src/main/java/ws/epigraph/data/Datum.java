/* Created by yegor on 8/3/16. */

package ws.epigraph.data;

import ws.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;


public interface Datum {

  @NotNull DatumType type();

  static @Nullable Datum.Imm toImmutable(@Nullable Datum datum) { return datum == null ? null : datum.toImmutable(); }

  @NotNull Datum.Raw _raw();

  @NotNull Datum.Imm toImmutable();

  @NotNull Val asValue();


  abstract class Impl<MyType extends DatumType> implements Datum {

    private final @NotNull MyType type;

    protected Impl(@NotNull MyType type) { this.type = type; }

    @Override
    public final @NotNull MyType type() { return type; }

  }


  interface Raw extends Datum {

    @Override
    @NotNull Datum.Imm.Raw toImmutable();

    @Override
    @NotNull Val.Raw asValue();

  }


  interface Static extends Datum {

    @Override
    @NotNull Datum.Imm.Static toImmutable();

    @Override
    @NotNull Val.Static asValue();

  }


  interface Imm extends Datum, Immutable {

    @Override
    @NotNull Val.Imm asValue();


    interface Raw extends Datum.Imm, Datum.Raw {

      @Override
      @NotNull Val.Imm.Raw asValue();

    }


    interface Static extends Datum.Imm, Datum.Static {

      @Override
      @NotNull Val.Imm.Static asValue();

    }


  }


  interface Builder extends Datum, Mutable {


    interface Raw extends Datum.Builder, Datum.Raw {}


    interface Static<MyImmDatum extends Datum.Imm.Static> extends Datum.Builder, Datum.Static {

      @Override
      @NotNull MyImmDatum toImmutable();

    }


  }


  interface Mut extends Datum, Mutable {


    interface Raw extends Datum.Mut, Datum.Raw {}


    interface Static<MyImmDatum extends Datum.Imm.Static> extends Datum.Mut, Datum.Static {

      @Override
      @NotNull MyImmDatum toImmutable();

    }


  }


}
