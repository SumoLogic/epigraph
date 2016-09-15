/* Created by yegor on 9/6/16. */

package io.epigraph.data;

import io.epigraph.types.BooleanType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;


public interface BooleanDatum extends PrimitiveDatum<Boolean> {

  @Override
  @NotNull BooleanType type();

  @Override
  @NotNull BooleanDatum.Raw _raw();

  @Override
  @NotNull BooleanDatum.Imm toImmutable();

  @NotNull Boolean getVal();


  abstract class Impl extends PrimitiveDatum.Impl<Boolean, BooleanType> implements BooleanDatum {

    protected Impl(@NotNull BooleanType type) { super(type); }

  }


  interface Raw extends BooleanDatum, PrimitiveDatum.Raw<Boolean> {

    @Override
    @NotNull BooleanDatum.Imm.Raw toImmutable();

  }


  interface Static extends BooleanDatum, PrimitiveDatum.Static<Boolean> {

    @Override
    @NotNull BooleanDatum.Imm.Static toImmutable();

  }


  interface Imm extends BooleanDatum, PrimitiveDatum.Imm<Boolean> {

    @Override
    @NotNull BooleanDatum.Imm.Raw _raw();


    final class Raw extends BooleanDatum.Impl implements BooleanDatum.Imm, BooleanDatum.Raw, PrimitiveDatum.Imm.Raw<Boolean> {

      private final @NotNull Boolean val;

      public Raw(@NotNull BooleanType type, @NotNull BooleanDatum prototype) {
        super(type);
        // TODO check types are compatible
        this.val = prototype.getVal();
      }

      @Override
      public @NotNull BooleanDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull BooleanDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Boolean getVal() { return val; }

    }


    interface Static extends BooleanDatum.Imm, BooleanDatum.Static, PrimitiveDatum.Imm.Static<Boolean> {

      @Override
      @NotNull BooleanDatum.Imm.Static toImmutable();

      @Override
      @NotNull BooleanDatum.Imm.Raw _raw();


      // TODO parameterize with MyImmDatum
      abstract class Impl extends BooleanDatum.Impl implements BooleanDatum.Imm.Static {

        private final @NotNull BooleanDatum.Imm.Raw raw;

        protected Impl(@NotNull BooleanType type, @NotNull BooleanDatum.Imm.Raw raw) {
          super(type); // TODO take static type separately?
          this.raw = raw; // TODO validate raw is kosher?
        }

        @Override
        public @NotNull Boolean getVal() { return raw.getVal(); }

        @Override
        public @NotNull BooleanDatum.Imm.Static toImmutable() { return this; }

        @Override
        public @NotNull BooleanDatum.Imm.Raw _raw() { return raw; }

      }


    }


  }


  abstract class Builder extends BooleanDatum.Impl implements PrimitiveDatum.Builder<Boolean> {

    protected Builder(@NotNull BooleanType type) { super(type); }

    public abstract void setVal(@NotNull Boolean val);

    @Override
    public abstract @NotNull BooleanDatum.Builder.Raw _raw();


    public static final class Raw extends BooleanDatum.Builder implements BooleanDatum.Raw, PrimitiveDatum.Builder.Raw<Boolean> {

      private @NotNull Boolean val;

      public Raw(@NotNull BooleanType type, @NotNull Boolean val) {
        super(type);
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull Boolean getVal() { return val; }

      @Override
      public void setVal(@NotNull Boolean val) {
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull BooleanDatum.Imm.Raw toImmutable() { return new BooleanDatum.Imm.Raw(type(), this); }

      @Override
      public @NotNull BooleanDatum.Builder.Raw _raw() { return this; }

    }


    public static abstract class Static<MyImmDatum extends BooleanDatum.Imm.Static> extends BooleanDatum.Builder
        implements BooleanDatum.Static, PrimitiveDatum.Builder.Static<Boolean, MyImmDatum> {

      private final @NotNull BooleanDatum.Builder.Raw raw;

      private final @NotNull Function<BooleanDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull BooleanType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull BooleanDatum.Builder.Raw raw,
          @NotNull Function<BooleanDatum.Imm.Raw, MyImmDatum> immDatumConstructor
      ) {
        super(type);
        // TODO check type equality
        this.raw = raw;
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull Boolean getVal() { return raw.getVal(); }

      @Override
      public final void setVal(@NotNull Boolean val) { raw.setVal(val); }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull BooleanDatum.Builder.Raw _raw() { return raw; }

    }


  }


  abstract class Mut extends BooleanDatum.Impl implements PrimitiveDatum.Mut<Boolean> {

    protected Mut(@NotNull BooleanType type) { super(type); }

    public abstract void setVal(@NotNull Boolean val);

    @Override
    public abstract @NotNull BooleanDatum.Mut.Raw _raw();


    public static final class Raw extends BooleanDatum.Mut implements BooleanDatum.Raw, PrimitiveDatum.Mut.Raw<Boolean> {

      private @NotNull Boolean val;

      public Raw(@NotNull BooleanType type, @NotNull Boolean val) {
        super(type);
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull Boolean getVal() { return val; }

      @Override
      public void setVal(@NotNull Boolean val) {
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull BooleanDatum.Imm.Raw toImmutable() { return new BooleanDatum.Imm.Raw(type(), this); }

      @Override
      public @NotNull BooleanDatum.Mut.Raw _raw() { return this; }

    }


    public static abstract class Static<MyImmDatum extends BooleanDatum.Imm.Static> extends BooleanDatum.Mut
        implements BooleanDatum.Static, PrimitiveDatum.Mut.Static<Boolean, MyImmDatum> {

      private final @NotNull BooleanDatum.Mut.Raw raw;

      private final @NotNull Function<BooleanDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull BooleanType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull BooleanDatum.Mut.Raw raw,
          @NotNull Function<BooleanDatum.Imm.Raw, MyImmDatum> immDatumConstructor
      ) {
        super(type);
        // TODO check type equality
        this.raw = raw;
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull Boolean getVal() { return raw.getVal(); }

      @Override
      public final void setVal(@NotNull Boolean val) { raw.setVal(val); }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull BooleanDatum.Mut.Raw _raw() { return raw; }

    }


  }


}
