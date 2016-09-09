/* Created by yegor on 9/6/16. */

package io.epigraph.data;

import io.epigraph.types.LongType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;


public interface LongDatum extends PrimitiveDatum<Long> {

  @Override
  @NotNull LongType type();

  @Override
  @NotNull LongDatum.Raw _raw();

  @Override
  @NotNull LongDatum.Imm toImmutable();

  @NotNull Long getVal();


  abstract class Impl extends PrimitiveDatum.Impl<Long, LongType> implements LongDatum {

    protected Impl(@NotNull LongType type) { super(type); }

  }


  interface Raw extends LongDatum, PrimitiveDatum.Raw<Long> {

    @Override
    @NotNull LongDatum.Imm.Raw toImmutable();

  }


  interface Static extends LongDatum, PrimitiveDatum.Static<Long> {

    @Override
    @NotNull LongDatum.Imm.Static toImmutable();

  }


  interface Imm extends LongDatum, PrimitiveDatum.Imm<Long> {

    @Override
    @NotNull LongDatum.Imm.Raw _raw();


    final class Raw extends LongDatum.Impl implements LongDatum.Imm, LongDatum.Raw, PrimitiveDatum.Imm.Raw<Long> {

      private final @NotNull Long val;

      public Raw(@NotNull LongType type, @NotNull LongDatum prototype) {
        super(type);
        // TODO check types are compatible
        this.val = prototype.getVal();
      }

      @Override
      public @NotNull LongDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull LongDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Long getVal() { return val; }

    }


    interface Static extends LongDatum.Imm, LongDatum.Static, PrimitiveDatum.Imm.Static<Long> {

      @Override
      @NotNull LongDatum.Imm.Static toImmutable();

      @Override
      @NotNull LongDatum.Imm.Raw _raw();


      // TODO parameterize with MyImmDatum
      abstract class Impl extends LongDatum.Impl implements LongDatum.Imm.Static {

        private final @NotNull LongDatum.Imm.Raw raw;

        protected Impl(@NotNull LongType type, @NotNull LongDatum.Imm.Raw raw) {
          super(type); // TODO take static type separately?
          this.raw = raw; // TODO validate raw is kosher?
        }

        @Override
        public @NotNull Long getVal() { return raw.getVal(); }

        @Override
        public @NotNull LongDatum.Imm.Static toImmutable() { return this; }

        @Override
        public @NotNull LongDatum.Imm.Raw _raw() { return raw; }

      }


    }


  }


  abstract class Mut extends LongDatum.Impl implements PrimitiveDatum.Mut<Long> {

    protected Mut(@NotNull LongType type) { super(type); }

    public abstract void setVal(@NotNull Long val);

    @Override
    public abstract @NotNull LongDatum.Mut.Raw _raw();


    public static final class Raw extends LongDatum.Mut implements LongDatum.Raw, PrimitiveDatum.Mut.Raw<Long> {

      private @NotNull Long val;

      public Raw(@NotNull LongType type, @NotNull Long val) {
        super(type);
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull Long getVal() { return val; }

      @Override
      public void setVal(@NotNull Long val) {
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull LongDatum.Imm.Raw toImmutable() { return new LongDatum.Imm.Raw(type(), this); }

      @Override
      public @NotNull LongDatum.Mut.Raw _raw() { return this; }

    }


    public static abstract class Static<MyImmDatum extends LongDatum.Imm.Static> extends LongDatum.Mut
        implements LongDatum.Static, PrimitiveDatum.Mut.Static<Long, MyImmDatum> {

      private final @NotNull LongDatum.Mut.Raw raw;

      private final @NotNull Function<LongDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull LongType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull LongDatum.Mut.Raw raw,
          @NotNull Function<LongDatum.Imm.Raw, MyImmDatum> immDatumConstructor
      ) {
        super(type);
        // TODO check type equality
        this.raw = raw;
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull Long getVal() { return raw.getVal(); }

      @Override
      public final void setVal(@NotNull Long val) { raw.setVal(val); }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull LongDatum.Mut.Raw _raw() { return raw; }

    }


  }


}
