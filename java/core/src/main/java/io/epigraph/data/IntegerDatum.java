/* Created by yegor on 8/4/16. */

package io.epigraph.data;

import io.epigraph.types.IntegerType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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

      private final @NotNull Val.Imm.Raw value = new Val.Imm.Raw.DatumVal(this);

      private final int hashCode;

      public Raw(@NotNull IntegerDatum.Builder.Raw mutable) {
        super(mutable.type());
        val = mutable.getVal(); // TODO copy metadata
        hashCode = Objects.hash(type(), val);
      }

      @Override
      public @NotNull IntegerDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull IntegerDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Integer getVal() { return val; }

      @Override
      public @NotNull Val.Imm.Raw asValue() { return value; }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerDatum)) return false;
        if (o instanceof Immutable && hashCode != o.hashCode()) return false;
        IntegerDatum that = (IntegerDatum) o;
        return type().equals(that.type()) && val.equals(that._raw().getVal());
      }

      @Override
      public final int hashCode() { return hashCode; }

    }


    interface Static extends IntegerDatum.Imm, IntegerDatum.Static, PrimitiveDatum.Imm.Static<Integer> {

      @Override
      @NotNull IntegerDatum.Imm.Static toImmutable();

      @Override
      @NotNull IntegerDatum.Imm.Raw _raw();


      abstract class Impl<MyImmDatum extends IntegerDatum.Imm.Static, MyImmVal extends Val.Imm.Static>
          extends IntegerDatum.Impl implements IntegerDatum.Imm.Static {

        private final @NotNull IntegerDatum.Imm.Raw raw;

        private final @NotNull MyImmVal value;

        protected Impl(
            @NotNull IntegerType type,
            @NotNull IntegerDatum.Imm.Raw raw,
            @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor
        ) {
          super(type); // TODO take static type separately?
          this.raw = raw; // TODO validate raw is kosher?
          this.value = immValConstructor.apply(new Val.Imm.Raw.DatumVal(this));
        }

        @Override
        public @NotNull Integer getVal() { return raw.getVal(); }

        @Override
        public @NotNull MyImmDatum toImmutable() { return (MyImmDatum) this; }

        @Override
        public final @NotNull IntegerDatum.Imm.Raw _raw() { return raw; }

        @Override
        public @NotNull MyImmVal asValue() { return value; }

        @Override
        public final int hashCode() { return raw.hashCode(); }

        @Override
        public final boolean equals(Object obj) { return raw.equals(obj); }

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

      private final @NotNull Val.Builder.Raw value = new Val.Builder.Raw.DatumVal(this);

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
      public @NotNull IntegerDatum.Imm.Raw toImmutable() { return new IntegerDatum.Imm.Raw(this); }

      @Override
      public @NotNull IntegerDatum.Builder.Raw _raw() { return this; }

      @Override
      public @NotNull Val.Builder.Raw asValue() { return value; }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerDatum)) return false;
        IntegerDatum that = (IntegerDatum) o;
        return type().equals(that.type()) && val.equals(that._raw().getVal());
      }

      @Override
      public final int hashCode() { return Objects.hash(type(), val); }

    }


    public static abstract class Static<
        MyImmDatum extends IntegerDatum.Imm.Static,
        MyBuilderVal extends Val.Builder.Static
        > extends IntegerDatum.Builder
        implements IntegerDatum.Static, PrimitiveDatum.Builder.Static<Integer, MyImmDatum> {

      private final @NotNull IntegerDatum.Builder.Raw raw;

      private final @NotNull MyBuilderVal value;

      private final @NotNull Function<IntegerDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull IntegerType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull IntegerDatum.Builder.Raw raw,
          @NotNull Function<IntegerDatum.Imm.Raw, MyImmDatum> immDatumConstructor,
          @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyBuilderVal> builderValConstructor
      ) {
        super(type);
        // TODO check type equality
        this.raw = raw;
        this.value = builderValConstructor.apply(new Val.Builder.Raw.DatumVal(this));
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

      @Override
      public @NotNull MyBuilderVal asValue() { return value; }

      @Override
      public final int hashCode() { return raw.hashCode(); }

      @Override
      public final boolean equals(Object obj) { return raw.equals(obj); }

    }


  }


}
