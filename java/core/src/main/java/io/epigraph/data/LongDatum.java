/* Created by yegor on 9/6/16. */

package io.epigraph.data;

import io.epigraph.types.LongType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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

      private final @NotNull Val.Imm.Raw value = new Val.Imm.Raw.DatumVal(this);

      private final int hashCode;

      public Raw(@NotNull LongDatum.Builder.Raw mutable) {
        super(mutable.type());
        val = mutable.getVal(); // TODO copy metadata
        hashCode = Objects.hash(type(), val);
      }

      @Override
      public @NotNull LongDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull LongDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Long getVal() { return val; }

      @Override
      public @NotNull Val.Imm.Raw asValue() { return value; }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongDatum)) return false;
        if (o instanceof Immutable && hashCode != o.hashCode()) return false;
        LongDatum that = (LongDatum) o;
        return type().equals(that.type()) && val.equals(that._raw().getVal());
      }

      @Override
      public final int hashCode() { return hashCode; }

    }


    interface Static extends LongDatum.Imm, LongDatum.Static, PrimitiveDatum.Imm.Static<Long> {

      @Override
      @NotNull LongDatum.Imm.Static toImmutable();

      @Override
      @NotNull LongDatum.Imm.Raw _raw();


      abstract class Impl<MyImmDatum extends LongDatum.Imm.Static, MyImmVal extends Val.Imm.Static>
          extends LongDatum.Impl implements LongDatum.Imm.Static {

        private final @NotNull LongDatum.Imm.Raw raw;

        private final @NotNull MyImmVal value;

        protected Impl(
            @NotNull LongType type,
            @NotNull LongDatum.Imm.Raw raw,
            @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor
        ) {
          super(type); // TODO take static type separately?
          this.raw = raw; // TODO validate raw is kosher?
          this.value = immValConstructor.apply(new Val.Imm.Raw.DatumVal(this));
        }

        @Override
        public @NotNull Long getVal() { return raw.getVal(); }

        @Override
        public @NotNull MyImmDatum toImmutable() { return (MyImmDatum) this; }

        @Override
        public final @NotNull LongDatum.Imm.Raw _raw() { return raw; }

        @Override
        public @NotNull MyImmVal asValue() { return value; }

        @Override
        public final int hashCode() { return raw.hashCode(); }

        @Override
        public final boolean equals(Object obj) { return raw.equals(obj); }

      }


    }


  }


  abstract class Builder extends LongDatum.Impl implements PrimitiveDatum.Builder<Long> {

    protected Builder(@NotNull LongType type) { super(type); }

    public abstract void setVal(@NotNull Long val);

    @Override
    public abstract @NotNull LongDatum.Builder.Raw _raw();


    public static final class Raw extends LongDatum.Builder implements LongDatum.Raw, PrimitiveDatum.Builder.Raw<Long> {

      private @NotNull Long val;

      private final @NotNull Val.Builder.Raw value = new Val.Builder.Raw.DatumVal(this);

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
      public @NotNull LongDatum.Imm.Raw toImmutable() { return new LongDatum.Imm.Raw(this); }

      @Override
      public @NotNull LongDatum.Builder.Raw _raw() { return this; }

      @Override
      public @NotNull Val.Builder.Raw asValue() { return value; }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongDatum)) return false;
        LongDatum that = (LongDatum) o;
        return type().equals(that.type()) && val.equals(that._raw().getVal());
      }

      @Override
      public final int hashCode() { return Objects.hash(type(), val); }

    }


    public static abstract class Static<
        MyImmDatum extends LongDatum.Imm.Static,
        MyBuilderVal extends Val.Builder.Static
        > extends LongDatum.Builder
        implements LongDatum.Static, PrimitiveDatum.Builder.Static<Long, MyImmDatum> {

      private final @NotNull LongDatum.Builder.Raw raw;

      private final @NotNull MyBuilderVal value;

      private final @NotNull Function<LongDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull LongType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull LongDatum.Builder.Raw raw,
          @NotNull Function<LongDatum.Imm.Raw, MyImmDatum> immDatumConstructor,
          @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyBuilderVal> builderValConstructor
      ) {
        super(type);
        // TODO check type equality
        this.raw = raw;
        this.value = builderValConstructor.apply(new Val.Builder.Raw.DatumVal(this));
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull Long getVal() { return raw.getVal(); }

      @Override
      public final void setVal(@NotNull Long val) { raw.setVal(val); }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull LongDatum.Builder.Raw _raw() { return raw; }

      @Override
      public @NotNull MyBuilderVal asValue() { return value; }

      @Override
      public final int hashCode() { return raw.hashCode(); }

      @Override
      public final boolean equals(Object obj) { return raw.equals(obj); }

    }


  }


}
