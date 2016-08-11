/* Created by yegor on 8/4/16. */

package io.epigraph.data;

import io.epigraph.types.StringType;
import org.jetbrains.annotations.NotNull;


public interface StringDatum extends Datum {

  @Override
  @NotNull StringType type();

  @Override
  @NotNull StringDatum.Raw _raw();

  @Override
  @NotNull StringDatum.Imm toImmutable();

  @NotNull String getVal();


  abstract class Impl extends Datum.Impl<StringType> implements StringDatum {

    protected Impl(@NotNull StringType type) { super(type); }

  }


  interface Raw extends StringDatum, Datum.Raw {

    @Override
    @NotNull StringDatum.Imm.Raw toImmutable();

  }


  interface Static extends StringDatum, Datum.Static {

    @Override
    @NotNull StringDatum.Imm.Static toImmutable();

  }


  interface Imm extends StringDatum, Datum.Imm {

    @Override
    @NotNull StringDatum.Imm.Raw _raw();


    final class Raw extends StringDatum.Impl implements StringDatum.Imm, StringDatum.Raw, Datum.Imm.Raw {

      private final @NotNull String val;

      public Raw(@NotNull StringType type, @NotNull StringDatum prototype) {
        super(type);
        this.val = prototype.getVal();
      }

      @Override
      public @NotNull StringDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull StringDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull String getVal() { return val; }

    }


    interface Static extends StringDatum.Imm, StringDatum.Static, Datum.Imm.Static {

      @Override
      @NotNull StringDatum.Imm.Static toImmutable();

      @Override
      @NotNull StringDatum.Imm.Raw _raw();


      // TODO parameterize with MyImmDatum
      abstract class Impl extends StringDatum.Impl implements StringDatum.Imm.Static {

        private final @NotNull StringDatum.Imm.Raw raw;

        protected Impl(@NotNull StringType type, @NotNull StringDatum.Imm.Raw raw) {
          super(type); // TODO take static type separately?
          this.raw = raw; // TODO validate raw is kosher?
        }

        @Override
        public @NotNull String getVal() { return raw.getVal(); }

        @Override
        public @NotNull StringDatum.Imm.Static toImmutable() { return this; }

        @Override
        public @NotNull StringDatum.Imm.Raw _raw() { return raw; }

      }


    }


  }


  abstract class Mut extends StringDatum.Impl implements Datum.Mut {

    protected Mut(@NotNull StringType type) { super(type); }

    public abstract void setVal(@NotNull String val);

    @Override
    public abstract @NotNull StringDatum.Mut.Raw _raw();


    public static final class Raw extends StringDatum.Mut implements StringDatum.Raw, Datum.Mut.Raw {

      private @NotNull String val;

      public Raw(@NotNull StringType type, @NotNull String val) {
        super(type);
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull String getVal() { return val; }

      @Override
      public void setVal(@NotNull String val) {
        // TODO validate vs type validation rules (once available)
        this.val = /*this.val = type().validate*/(val);
      }

      @Override
      public @NotNull StringDatum.Imm.Raw toImmutable() { return new StringDatum.Imm.Raw(type(), this); }

      @Override
      public @NotNull StringDatum.Mut.Raw _raw() { return this; }

    }


    public static abstract class Static<MyImm extends StringDatum.Imm.Static> extends StringDatum.Mut
        implements StringDatum.Static, Datum.Mut.Static {

      private final @NotNull StringDatum.Mut.Raw raw;

      protected Static(@NotNull StringDatum.Mut.Raw raw) {
        super(raw.type()); // TODO take static type separately
        // TODO check types are equal
        this.raw = raw; // TODO validate raw data is kosher?
      }

      @Override
      public @NotNull String getVal() { return raw.getVal(); }

      @Override
      public void setVal(@NotNull String val) { raw.setVal(val); }

      @Override
      public abstract @NotNull MyImm toImmutable(); // TODO either delegate to static type or leave abstract to be implemented by concrete subclass

      @Override
      public @NotNull StringDatum.Mut.Raw _raw() { return raw; }

    }


  }


}
