/* Created by yegor on 9/6/16. */

package io.epigraph.data;

import io.epigraph.types.BooleanType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;


public interface BooleanDatum extends Datum {

  @Override
  @NotNull BooleanType type();

  @Override
  @NotNull BooleanDatum.Raw _raw();

  @Override
  @NotNull BooleanDatum.Imm toImmutable();

  @NotNull Boolean getVal();


  abstract class Impl extends Datum.Impl<BooleanType> implements BooleanDatum {

    protected Impl(@NotNull BooleanType type) { super(type); }

  }


  interface Raw extends BooleanDatum, Datum.Raw {

    @Override
    @NotNull BooleanDatum.Imm.Raw toImmutable();

  }


  interface Static extends BooleanDatum, Datum.Static {

    @Override
    @NotNull BooleanDatum.Imm.Static toImmutable();

  }


  interface Imm extends BooleanDatum, Datum.Imm {

    @Override
    @NotNull BooleanDatum.Imm.Raw _raw();


    final class Raw extends BooleanDatum.Impl implements BooleanDatum.Imm, BooleanDatum.Raw, Datum.Imm.Raw {

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


    interface Static extends BooleanDatum.Imm, BooleanDatum.Static, Datum.Imm.Static {

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


  abstract class Mut extends BooleanDatum.Impl implements Datum.Mut {

    protected Mut(@NotNull BooleanType type) { super(type); }

    public abstract void setVal(@NotNull Boolean val);

    @Override
    public abstract @NotNull BooleanDatum.Mut.Raw _raw();


    public static final class Raw extends BooleanDatum.Mut implements BooleanDatum.Raw, Datum.Mut.Raw {

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
        implements BooleanDatum.Static, Datum.Mut.Static {

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
