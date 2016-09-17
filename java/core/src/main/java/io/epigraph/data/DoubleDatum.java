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

      private @NotNull Val.Imm.Raw value = new Val.Imm.Raw.DatumVal(this);

      public Raw(@NotNull DoubleType type, @NotNull DoubleDatum prototype) {
        super(type);
        // TODO check types are compatible
        this.val = prototype.getVal(); // TODO copy metadata
      }

      @Override
      public @NotNull DoubleDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull DoubleDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Double getVal() { return val; }

      @Override
      public @NotNull Val.Imm.Raw asValue() { return value; }

    }


    interface Static extends DoubleDatum.Imm, DoubleDatum.Static, PrimitiveDatum.Imm.Static<Double> {

      @Override
      @NotNull DoubleDatum.Imm.Static toImmutable();

      @Override
      @NotNull DoubleDatum.Imm.Raw _raw();


      abstract class Impl<MyImmDatum extends DoubleDatum.Imm.Static, MyImmVal extends Val.Imm.Static>
          extends DoubleDatum.Impl implements DoubleDatum.Imm.Static {

        private final @NotNull DoubleDatum.Imm.Raw raw;

        private final @NotNull MyImmVal value;

        protected Impl(
            @NotNull DoubleType type,
            @NotNull DoubleDatum.Imm.Raw raw,
            @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor
        ) {
          super(type); // TODO take static type separately?
          this.raw = raw; // TODO validate raw is kosher?
          this.value = immValConstructor.apply(raw.asValue());
        }

        @Override
        public @NotNull Double getVal() { return raw.getVal(); }

        @Override
        public @NotNull MyImmDatum toImmutable() { return (MyImmDatum) this; }

        @Override
        public @NotNull DoubleDatum.Imm.Raw _raw() { return raw; }

        @Override
        public @NotNull MyImmVal asValue() { return value; }

      }


    }


  }


  abstract class Builder extends DoubleDatum.Impl implements PrimitiveDatum.Builder<Double> {

    protected Builder(@NotNull DoubleType type) { super(type); }

    public abstract void setVal(@NotNull Double val);

    @Override
    public abstract @NotNull DoubleDatum.Builder.Raw _raw();


    public static final class Raw extends DoubleDatum.Builder implements DoubleDatum.Raw, PrimitiveDatum.Builder.Raw<Double> {

      private @NotNull Double val;

      private final @NotNull Val.Builder.Raw value = new Val.Builder.Raw.DatumVal(this);

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
      public @NotNull DoubleDatum.Builder.Raw _raw() { return this; }

      @Override
      public @NotNull Val.Builder.Raw asValue() { return value; }

    }


    public static abstract class Static<
        MyImmDatum extends DoubleDatum.Imm.Static,
        MyBuilderVal extends Val.Builder.Static
        > extends DoubleDatum.Builder
        implements DoubleDatum.Static, PrimitiveDatum.Builder.Static<Double, MyImmDatum> {

      private final @NotNull DoubleDatum.Builder.Raw raw;

      private final @NotNull MyBuilderVal value;

      private final @NotNull Function<DoubleDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull DoubleType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull DoubleDatum.Builder.Raw raw,
          @NotNull Function<DoubleDatum.Imm.Raw, MyImmDatum> immDatumConstructor,
          @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyBuilderVal> builderValConstructor
      ) {
        super(type);
        // TODO check type equality
        this.raw = raw;
        this.value = builderValConstructor.apply(raw.asValue());
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull Double getVal() { return raw.getVal(); }

      @Override
      public final void setVal(@NotNull Double val) { raw.setVal(val); }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull DoubleDatum.Builder.Raw _raw() { return raw; }

      @Override
      public @NotNull MyBuilderVal asValue() { return value; }

    }


  }


//  abstract class Mut extends DoubleDatum.Impl implements PrimitiveDatum.Mut<Double> {
//
//    protected Mut(@NotNull DoubleType type) { super(type); }
//
//    public abstract void setVal(@NotNull Double val);
//
//    @Override
//    public abstract @NotNull DoubleDatum.Mut.Raw _raw();
//
//
//    public static final class Raw extends DoubleDatum.Mut implements DoubleDatum.Raw, PrimitiveDatum.Mut.Raw<Double> {
//
//      private @NotNull Double val;
//
//      public Raw(@NotNull DoubleType type, @NotNull Double val) {
//        super(type);
//        // TODO validate vs type validation rules (once available)
//        this.val = /*this.val = type().validate*/(val);
//      }
//
//      @Override
//      public @NotNull Double getVal() { return val; }
//
//      @Override
//      public void setVal(@NotNull Double val) {
//        // TODO validate vs type validation rules (once available)
//        this.val = /*this.val = type().validate*/(val);
//      }
//
//      @Override
//      public @NotNull DoubleDatum.Imm.Raw toImmutable() { return new DoubleDatum.Imm.Raw(type(), this); }
//
//      @Override
//      public @NotNull DoubleDatum.Mut.Raw _raw() { return this; }
//
//    }
//
//
//    public static abstract class Static<MyImmDatum extends DoubleDatum.Imm.Static> extends DoubleDatum.Mut
//        implements DoubleDatum.Static, PrimitiveDatum.Mut.Static<Double, MyImmDatum> {
//
//      private final @NotNull DoubleDatum.Mut.Raw raw;
//
//      private final @NotNull Function<DoubleDatum.Imm.Raw, MyImmDatum> immDatumConstructor;
//
//      protected Static(
//          @NotNull DoubleType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
//          @NotNull DoubleDatum.Mut.Raw raw,
//          @NotNull Function<DoubleDatum.Imm.Raw, MyImmDatum> immDatumConstructor
//      ) {
//        super(type);
//        // TODO check type equality
//        this.raw = raw;
//        this.immDatumConstructor = immDatumConstructor;
//      }
//
//      @Override
//      public final @NotNull Double getVal() { return raw.getVal(); }
//
//      @Override
//      public final void setVal(@NotNull Double val) { raw.setVal(val); }
//
//      @Override
//      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }
//
//      @Override
//      public final @NotNull DoubleDatum.Mut.Raw _raw() { return raw; }
//
//    }
//
//
//  }


}
