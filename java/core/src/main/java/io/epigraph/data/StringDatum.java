/* Created by yegor on 8/4/16. */

package io.epigraph.data;

import io.epigraph.types.StringType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;


public interface StringDatum extends PrimitiveDatum<String> {

  @Override
  @NotNull StringType type();

  @Override
  @NotNull StringDatum.Raw _raw();

  @Override
  @NotNull StringDatum.Imm toImmutable();

  @NotNull String getVal();


  abstract class Impl extends PrimitiveDatum.Impl<String, StringType> implements StringDatum {

    protected Impl(@NotNull StringType type) { super(type); }

  }


  interface Raw extends StringDatum, PrimitiveDatum.Raw<String> {

    @Override
    @NotNull StringDatum.Imm.Raw toImmutable();

  }


  interface Static extends StringDatum, PrimitiveDatum.Static<String> {

    @Override
    @NotNull StringDatum.Imm.Static toImmutable();

  }


  interface Imm extends StringDatum, PrimitiveDatum.Imm<String> {

    @Override
    @NotNull StringDatum.Imm.Raw _raw();


    final class Raw extends StringDatum.Impl implements StringDatum.Imm, StringDatum.Raw, PrimitiveDatum.Imm.Raw<String> {

      private final @NotNull String val;

      private @NotNull Val.Imm.Raw value = new Val.Imm.Raw.DatumVal(this);

      public Raw(@NotNull StringType type, @NotNull StringDatum prototype) {
        super(type);
        // TODO check types are compatible
        this.val = prototype.getVal(); // TODO copy metadata
      }

      @Override
      public @NotNull StringDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull StringDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull String getVal() { return val; }

      @Override
      public @NotNull Val.Imm.Raw asValue() { return value; }

    }


    interface Static extends StringDatum.Imm, StringDatum.Static, PrimitiveDatum.Imm.Static<String> {

      @Override
      @NotNull StringDatum.Imm.Static toImmutable();

      @Override
      @NotNull StringDatum.Imm.Raw _raw();


      abstract class Impl<MyImmDatum extends StringDatum.Imm.Static, MyImmVal extends Val.Imm.Static>
          extends StringDatum.Impl implements StringDatum.Imm.Static {

        private final @NotNull StringDatum.Imm.Raw raw;

        private final @NotNull MyImmVal value;

        protected Impl(
            @NotNull StringType type,
            @NotNull StringDatum.Imm.Raw raw,
            @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor
        ) {
          super(type); // TODO take static type separately?
          this.raw = raw; // TODO validate raw is kosher?
          this.value = immValConstructor.apply(raw.asValue());
        }

        @Override
        public @NotNull String getVal() { return raw.getVal(); }

        @Override
        public @NotNull MyImmDatum toImmutable() { return (MyImmDatum) this; }

        @Override
        public @NotNull StringDatum.Imm.Raw _raw() { return raw; }

        @Override
        public @NotNull MyImmVal asValue() { return value; }

      }


    }


  }


  abstract class Builder extends StringDatum.Impl implements PrimitiveDatum.Builder<String> {

    protected Builder(@NotNull StringType type) { super(type); }

    public abstract void setVal(@NotNull String val);

    @Override
    public abstract @NotNull StringDatum.Builder.Raw _raw();


    public static final class Raw extends StringDatum.Builder implements StringDatum.Raw, PrimitiveDatum.Builder.Raw<String> {

      private @NotNull String val;

      private final @NotNull Val.Builder.Raw value = new Val.Builder.Raw.DatumVal(this);

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
      public @NotNull StringDatum.Builder.Raw _raw() { return this; }

      @Override
      public @NotNull Val.Builder.Raw asValue() { return value; }

    }


    public static abstract class Static<
        MyImmDatum extends StringDatum.Imm.Static,
        MyBuilderVal extends Val.Builder.Static
        > extends StringDatum.Builder
        implements StringDatum.Static, PrimitiveDatum.Builder.Static<String, MyImmDatum> {

      private final @NotNull StringDatum.Builder.Raw raw;

      private final @NotNull MyBuilderVal value;

      private final @NotNull Function<StringDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull StringType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull StringDatum.Builder.Raw raw,
          @NotNull Function<StringDatum.Imm.Raw, MyImmDatum> immDatumConstructor,
          @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyBuilderVal> builderValConstructor
      ) {
        super(type);
        // TODO check type equality
        this.raw = raw;
        this.value = builderValConstructor.apply(raw.asValue());
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull String getVal() { return raw.getVal(); }

      @Override
      public final void setVal(@NotNull String val) { raw.setVal(val); }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull StringDatum.Builder.Raw _raw() { return raw; }

      @Override
      public @NotNull MyBuilderVal asValue() { return value; }

    }


  }


//  abstract class Mut extends StringDatum.Impl implements PrimitiveDatum.Mut<String> {
//
//    protected Mut(@NotNull StringType type) { super(type); }
//
//    public abstract void setVal(@NotNull String val);
//
//    @Override
//    public abstract @NotNull StringDatum.Mut.Raw _raw();
//
//
//    public static final class Raw extends StringDatum.Mut implements StringDatum.Raw, PrimitiveDatum.Mut.Raw<String> {
//
//      private @NotNull String val;
//
//      public Raw(@NotNull StringType type, @NotNull String val) {
//        super(type);
//        // TODO validate vs type validation rules (once available)
//        this.val = /*this.val = type().validate*/(val);
//      }
//
//      @Override
//      public @NotNull String getVal() { return val; }
//
//      @Override
//      public void setVal(@NotNull String val) {
//        // TODO validate vs type validation rules (once available)
//        this.val = /*this.val = type().validate*/(val);
//      }
//
//      @Override
//      public @NotNull StringDatum.Imm.Raw toImmutable() { return new StringDatum.Imm.Raw(type(), this); }
//
//      @Override
//      public @NotNull StringDatum.Mut.Raw _raw() { return this; }
//
//    }
//
//
//    public static abstract class Static<MyImmDatum extends StringDatum.Imm.Static> extends StringDatum.Mut
//        implements StringDatum.Static, PrimitiveDatum.Mut.Static<String, MyImmDatum> {
//
//      private final @NotNull StringDatum.Mut.Raw raw;
//
//      private final @NotNull Function<StringDatum.Imm.Raw, MyImmDatum> immDatumConstructor;
//
//      protected Static(
//          @NotNull StringType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
//          @NotNull StringDatum.Mut.Raw raw,
//          @NotNull Function<StringDatum.Imm.Raw, MyImmDatum> immDatumConstructor
//      ) {
//        super(type);
//        // TODO check type equality
//        this.raw = raw;
//        this.immDatumConstructor = immDatumConstructor;
//      }
//
//      @Override
//      public final @NotNull String getVal() { return raw.getVal(); }
//
//      @Override
//      public final void setVal(@NotNull String val) { raw.setVal(val); }
//
//      @Override
//      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }
//
//      @Override
//      public final @NotNull StringDatum.Mut.Raw _raw() { return raw; }
//
//    }
//
//
//  }


}
