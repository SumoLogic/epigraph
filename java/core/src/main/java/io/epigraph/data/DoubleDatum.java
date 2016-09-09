/* Created by yegor on 9/6/16. */

package io.epigraph.data;

import io.epigraph.types.DoubleType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;


public interface DoubleDatum extends PrimitiveDatum<Double> {

  @Override
  @NotNull DoubleType type();

  @Override
  @NotNull DoubleDatum.Raw _raw();

  @Override
  @NotNull DoubleDatum.Imm toImmutable();

  @NotNull Double getVal();


  abstract class Impl extends PrimitiveDatum.Impl<Double, DoubleType> implements DoubleDatum {

    protected Impl(@NotNull DoubleType type) { super(type); }

  }


  interface Raw extends DoubleDatum, PrimitiveDatum.Raw<Double> {

    @Override
    @NotNull DoubleDatum.Imm.Raw toImmutable();

  }


  interface Static extends DoubleDatum, PrimitiveDatum.Static<Double> {

    @Override
    @NotNull DoubleDatum.Imm.Static toImmutable();

  }


  interface Imm extends DoubleDatum, PrimitiveDatum.Imm<Double> {

    @Override
    @NotNull DoubleDatum.Imm.Raw _raw();


    final class Raw extends DoubleDatum.Impl implements DoubleDatum.Imm, DoubleDatum.Raw, PrimitiveDatum.Imm.Raw<Double> {

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


    interface Static extends DoubleDatum.Imm, DoubleDatum.Static, PrimitiveDatum.Imm.Static<Double> {

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


  abstract class Mut extends DoubleDatum.Impl implements PrimitiveDatum.Mut<Double> {

    protected Mut(@NotNull DoubleType type) { super(type); }

    public abstract void setVal(@NotNull Double val);

    @Override
    public abstract @NotNull DoubleDatum.Mut.Raw _raw();


    public static final class Raw extends DoubleDatum.Mut implements DoubleDatum.Raw, PrimitiveDatum.Mut.Raw<Double> {

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
        implements DoubleDatum.Static, PrimitiveDatum.Mut.Static<Double, MyImmDatum> {

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
