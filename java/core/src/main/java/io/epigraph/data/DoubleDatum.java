/* Created by yegor on 9/6/16. */

package io.epigraph.data;

import io.epigraph.types.DoubleType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;


public interface DoubleDatum extends Datum {

  @Override
  @NotNull DoubleType type();

  @Override
  @NotNull DoubleDatum.Raw _raw();

  @Override
  @NotNull DoubleDatum.Imm toImmutable();

  @NotNull Double getVal();


  abstract class Impl extends Datum.Impl<DoubleType> implements DoubleDatum {

    protected Impl(@NotNull DoubleType type) { super(type); }

  }


  interface Raw extends DoubleDatum, Datum.Raw {

    @Override
    @NotNull DoubleDatum.Imm.Raw toImmutable();

  }


  interface Static extends DoubleDatum, Datum.Static {

    @Override
    @NotNull DoubleDatum.Imm.Static toImmutable();

  }


  interface Imm extends DoubleDatum, Datum.Imm {

    @Override
    @NotNull DoubleDatum.Imm.Raw _raw();


    final class Raw extends DoubleDatum.Impl implements DoubleDatum.Imm, DoubleDatum.Raw, Datum.Imm.Raw {

      private final @NotNull Double val;

      public Raw(@NotNull DoubleType type, @NotNull DoubleDatum prototype) {
        super(type);
        // TODO check types are compatible
        this.val = prototype.getVal();
      }

      @Override
      public @NotNull DoubleDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull DoubleDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Double getVal() { return val; }

    }


    interface Static extends DoubleDatum.Imm, DoubleDatum.Static, Datum.Imm.Static {

      @Override
      @NotNull DoubleDatum.Imm.Static toImmutable();

      @Override
      @NotNull DoubleDatum.Imm.Raw _raw();


      // TODO parameterize with MyImmDatum
      abstract class Impl extends DoubleDatum.Impl implements DoubleDatum.Imm.Static {

        private final @NotNull DoubleDatum.Imm.Raw raw;

        protected Impl(@NotNull DoubleType type, @NotNull DoubleDatum.Imm.Raw raw) {
          super(type); // TODO take static type separately?
          this.raw = raw; // TODO validate raw is kosher?
        }

        @Override
        public @NotNull Double getVal() { return raw.getVal(); }

        @Override
        public @NotNull DoubleDatum.Imm.Static toImmutable() { return this; }

        @Override
        public @NotNull DoubleDatum.Imm.Raw _raw() { return raw; }

      }


    }


  }


  abstract class Mut extends DoubleDatum.Impl implements Datum.Mut {

    protected Mut(@NotNull DoubleType type) { super(type); }

    public abstract void setVal(@NotNull Double val);

    @Override
    public abstract @NotNull DoubleDatum.Mut.Raw _raw();


    public static final class Raw extends DoubleDatum.Mut implements DoubleDatum.Raw, Datum.Mut.Raw {

      private @NotNull Double val;

      public Raw(@NotNull DoubleType type, @NotNull Double val) {
        super(type);
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull Double getVal() { return val; }

      @Override
      public void setVal(@NotNull Double val) {
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull DoubleDatum.Imm.Raw toImmutable() { return new DoubleDatum.Imm.Raw(type(), this); }

      @Override
      public @NotNull DoubleDatum.Mut.Raw _raw() { return this; }

    }


    public static abstract class Static<MyImmDatum extends DoubleDatum.Imm.Static> extends DoubleDatum.Mut
        implements DoubleDatum.Static, Datum.Mut.Static {

      private final @NotNull DoubleDatum.Mut.Raw raw;

      private final @NotNull Function<DoubleDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull DoubleType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull DoubleDatum.Mut.Raw raw,
          @NotNull Function<DoubleDatum.Imm.Raw, MyImmDatum> immDatumConstructor
      ) {
        super(type);
        // TODO check type equality
        this.raw = raw;
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull Double getVal() { return raw.getVal(); }

      @Override
      public final void setVal(@NotNull Double val) { raw.setVal(val); }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull DoubleDatum.Mut.Raw _raw() { return raw; }

    }


  }


}
