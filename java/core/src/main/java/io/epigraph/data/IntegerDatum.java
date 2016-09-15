/* Created by yegor on 8/4/16. */

package io.epigraph.data;

import io.epigraph.types.IntegerType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;


public interface IntegerDatum extends PrimitiveDatum<Integer> {

  @Override
  @NotNull IntegerType type();

  @Override
  @NotNull IntegerDatum.Raw _raw();

  @Override
  @NotNull IntegerDatum.Imm toImmutable();

  @NotNull Integer getVal();


  abstract class Impl extends PrimitiveDatum.Impl<Integer, IntegerType> implements IntegerDatum {

    protected Impl(@NotNull IntegerType type) { super(type); }

  }


  interface Raw extends IntegerDatum, PrimitiveDatum.Raw<Integer> {

    @Override
    @NotNull IntegerDatum.Imm.Raw toImmutable();

  }


  interface Static extends IntegerDatum, PrimitiveDatum.Static<Integer> {

    @Override
    @NotNull IntegerDatum.Imm.Static toImmutable();

  }


  interface Imm extends IntegerDatum, PrimitiveDatum.Imm<Integer> {

    @Override
    @NotNull IntegerDatum.Imm.Raw _raw();


    final class Raw extends IntegerDatum.Impl implements IntegerDatum.Imm, IntegerDatum.Raw, PrimitiveDatum.Imm.Raw<Integer> {

      private final @NotNull Integer val;

      public Raw(@NotNull IntegerType type, @NotNull IntegerDatum prototype) {
        super(type);
        // TODO check types are compatible
        this.val = prototype.getVal();
      }

      @Override
      public @NotNull IntegerDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull IntegerDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Integer getVal() { return val; }

    }


    interface Static extends IntegerDatum.Imm, IntegerDatum.Static, PrimitiveDatum.Imm.Static<Integer> {

      @Override
      @NotNull IntegerDatum.Imm.Static toImmutable();

      @Override
      @NotNull IntegerDatum.Imm.Raw _raw();


      // TODO parameterize with MyImmDatum
      abstract class Impl extends IntegerDatum.Impl implements IntegerDatum.Imm.Static {

        private final @NotNull IntegerDatum.Imm.Raw raw;

        protected Impl(@NotNull IntegerType type, @NotNull IntegerDatum.Imm.Raw raw) {
          super(type); // TODO take static type separately?
          this.raw = raw; // TODO validate raw is kosher?
        }

        @Override
        public @NotNull Integer getVal() { return raw.getVal(); }

        @Override
        public @NotNull IntegerDatum.Imm.Static toImmutable() { return this; }

        @Override
        public @NotNull IntegerDatum.Imm.Raw _raw() { return raw; }

      }


    }


  }


  abstract class Builder extends IntegerDatum.Impl implements PrimitiveDatum.Builder<Integer> {

    protected Builder(@NotNull IntegerType type) { super(type); }

    public abstract void setVal(@NotNull Integer val);

    @Override
    public abstract @NotNull IntegerDatum.Builder.Raw _raw();


    public static final class Raw extends IntegerDatum.Builder implements IntegerDatum.Raw, PrimitiveDatum.Builder.Raw<Integer> {

      private @NotNull Integer val;

      public Raw(@NotNull IntegerType type, @NotNull Integer val) {
        super(type);
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull Integer getVal() { return val; }

      @Override
      public void setVal(@NotNull Integer val) {
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull IntegerDatum.Imm.Raw toImmutable() { return new IntegerDatum.Imm.Raw(type(), this); }

      @Override
      public @NotNull IntegerDatum.Builder.Raw _raw() { return this; }

    }


    public static abstract class Static<MyImmDatum extends IntegerDatum.Imm.Static> extends IntegerDatum.Builder
        implements IntegerDatum.Static, PrimitiveDatum.Builder.Static<Integer, MyImmDatum> {

      private final @NotNull IntegerDatum.Builder.Raw raw;

      private final @NotNull Function<IntegerDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull IntegerType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull IntegerDatum.Builder.Raw raw,
          @NotNull Function<IntegerDatum.Imm.Raw, MyImmDatum> immDatumConstructor
      ) {
        super(type);
        // TODO check type equality
        this.raw = raw;
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull Integer getVal() { return raw.getVal(); }

      @Override
      public final void setVal(@NotNull Integer val) { raw.setVal(val); }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull IntegerDatum.Builder.Raw _raw() { return raw; }

    }


  }


  abstract class Mut extends IntegerDatum.Impl implements PrimitiveDatum.Mut<Integer> {

    protected Mut(@NotNull IntegerType type) { super(type); }

    public abstract void setVal(@NotNull Integer val);

    @Override
    public abstract @NotNull IntegerDatum.Mut.Raw _raw();


    public static final class Raw extends IntegerDatum.Mut implements IntegerDatum.Raw, PrimitiveDatum.Mut.Raw<Integer> {

      private @NotNull Integer val;

      public Raw(@NotNull IntegerType type, @NotNull Integer val) {
        super(type);
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull Integer getVal() { return val; }

      @Override
      public void setVal(@NotNull Integer val) {
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull IntegerDatum.Imm.Raw toImmutable() { return new IntegerDatum.Imm.Raw(type(), this); }

      @Override
      public @NotNull IntegerDatum.Mut.Raw _raw() { return this; }

    }


    public static abstract class Static<MyImmDatum extends IntegerDatum.Imm.Static> extends IntegerDatum.Mut
        implements IntegerDatum.Static, PrimitiveDatum.Mut.Static<Integer, MyImmDatum> {

      private final @NotNull IntegerDatum.Mut.Raw raw;

      private final @NotNull Function<IntegerDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull IntegerType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull IntegerDatum.Mut.Raw raw,
          @NotNull Function<IntegerDatum.Imm.Raw, MyImmDatum> immDatumConstructor
      ) {
        super(type);
        // TODO check type equality
        this.raw = raw;
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull Integer getVal() { return raw.getVal(); }

      @Override
      public final void setVal(@NotNull Integer val) { raw.setVal(val); }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull IntegerDatum.Mut.Raw _raw() { return raw; }

    }


  }


}
