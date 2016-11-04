/* Created by yegor on 9/9/16. */

/* Created by yegor on 9/6/16. */

package ws.epigraph.data;

import ws.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;


public interface PrimitiveDatum<Native> extends Datum {

  @Override
  @NotNull PrimitiveType type();

  @Override
  @NotNull PrimitiveDatum.Raw _raw();

  @Override
  @NotNull PrimitiveDatum.Imm toImmutable();

  @NotNull Native getVal();


  abstract class Impl<Native, PT extends PrimitiveType> extends Datum.Impl<PT> implements PrimitiveDatum<Native> {

    protected Impl(@NotNull PT type) { super(type); }

    @Override
    public @NotNull String toString() { return getClass().getName() + "@" + getVal().toString(); }

  }


  interface Raw<Native> extends PrimitiveDatum<Native>, Datum.Raw {

    @Override
    @NotNull PrimitiveDatum.Imm.Raw toImmutable();

  }


  interface Static<Native> extends PrimitiveDatum<Native>, Datum.Static {

    @Override
    @NotNull PrimitiveDatum.Imm.Static toImmutable();

  }


  interface Imm<Native> extends PrimitiveDatum<Native>, Datum.Imm {

    @Override
    @NotNull PrimitiveDatum.Imm.Raw _raw();


    interface Raw<Native> extends PrimitiveDatum.Imm<Native>, PrimitiveDatum.Raw<Native>, Datum.Imm.Raw {

      @Override
      @NotNull PrimitiveDatum.Imm.Raw _raw();

    }


    interface Static<Native> extends PrimitiveDatum.Imm<Native>, PrimitiveDatum.Static<Native>, Datum.Imm.Static {

      @Override
      @NotNull PrimitiveDatum.Imm.Static toImmutable();

      @Override
      @NotNull PrimitiveDatum.Imm.Raw _raw();

    }


  }


  interface Builder<Native> extends PrimitiveDatum<Native>, Datum.Builder {

    void setVal(@NotNull Native val);

    @Override
    @NotNull PrimitiveDatum.Builder.Raw _raw();


    interface Raw<Native> extends PrimitiveDatum.Builder<Native>, PrimitiveDatum.Raw<Native>, Datum.Builder.Raw {}


    interface Static<Native, MyImmDatum extends PrimitiveDatum.Imm.Static<Native>>
        extends PrimitiveDatum.Builder<Native>, PrimitiveDatum.Static<Native>, Datum.Builder.Static<MyImmDatum> {}


  }


  interface Mut<Native> extends PrimitiveDatum<Native>, Datum.Mut {

    void setVal(@NotNull Native val);

    @Override
    @NotNull PrimitiveDatum.Mut.Raw _raw();


    interface Raw<Native> extends PrimitiveDatum.Mut<Native>, PrimitiveDatum.Raw<Native>, Datum.Mut.Raw {}


    interface Static<Native, MyImmDatum extends PrimitiveDatum.Imm.Static<Native>>
        extends PrimitiveDatum.Mut<Native>, PrimitiveDatum.Static<Native>, Datum.Mut.Static<MyImmDatum> {}


  }


}
