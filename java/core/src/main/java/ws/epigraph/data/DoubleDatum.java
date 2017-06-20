/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 9/6/16. */

package ws.epigraph.data;

import org.jetbrains.annotations.Nullable;
import ws.epigraph.types.DoubleType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;


public interface DoubleDatum extends PrimitiveDatum<Double> {

  @Override
  @NotNull DoubleType type();

  @Override
  @NotNull DoubleDatum.Raw _raw();

  @Override
  @NotNull DoubleDatum.Imm toImmutable();

  @Override
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

      private final @Nullable Datum.Imm meta;

      private final @NotNull Val.Imm.Raw value = new Val.Imm.Raw.DatumVal(this);

      private final int hashCode;

      @Override
      public @Nullable Datum.Imm meta() { return meta; }

      public Raw(@NotNull DoubleDatum.Builder.Raw mutable) {
        super(mutable.type());
        val = mutable.getVal();
        Datum _meta = mutable.meta();
        meta = _meta == null ? null : _meta.toImmutable();
        hashCode = Objects.hash(type(), val);
      }

      @Override
      public @NotNull DoubleDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull DoubleDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Double getVal() { return val; }

      @Override
      public @NotNull Val.Imm.Raw asValue() { return value; }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoubleDatum)) return false;
        if (o instanceof Immutable && hashCode != o.hashCode()) return false;
        DoubleDatum that = (DoubleDatum) o;
        return type().equals(that.type()) && val.equals(that._raw().getVal());
      }

      @Override
      public final int hashCode() { return hashCode; }

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
          this.value = immValConstructor.apply(new Val.Imm.Raw.DatumVal(this));
        }

        @Override
        public @NotNull Double getVal() { return raw.getVal(); }

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull MyImmDatum toImmutable() { return (MyImmDatum) this; }

        @Override
        public final @NotNull DoubleDatum.Imm.Raw _raw() { return raw; }

        @Override
        public @NotNull MyImmVal asValue() { return value; }

        @Override
        public final int hashCode() { return raw.hashCode(); }

        @Override
        public final boolean equals(Object obj) { return raw.equals(obj); }

      }


    }


  }


  abstract class Builder extends DoubleDatum.Impl implements PrimitiveDatum.Builder<Double> {

    protected Builder(@NotNull DoubleType type) { super(type); }

    @Override
    public abstract void setVal(@NotNull Double val);

    @Override
    public abstract @NotNull DoubleDatum.Builder.Raw _raw();


    public static final class Raw extends DoubleDatum.Builder implements DoubleDatum.Raw, PrimitiveDatum.Builder.Raw<Double> {

      private @NotNull Double val;

      private @Nullable Datum meta;

      private final @NotNull Val.Builder.Raw value = new Val.Builder.Raw.DatumVal(this);

      public Raw(@NotNull DoubleType type, @NotNull Double val) {
        super(type);
        this.val = val;
      }

      @Override
      public @NotNull Double getVal() { return val; }

      @Override
      public void setVal(@NotNull Double val) { this.val = val; }

      @Override
      public @Nullable Datum meta() { return meta; }

      @Override
      public @NotNull Datum.@NotNull Builder setMeta(final @Nullable Datum meta) {
        this.meta = type().checkMeta(meta);
        return this;
      }

      @Override
      public @NotNull DoubleDatum.Imm.Raw toImmutable() { return new DoubleDatum.Imm.Raw(this); }

      @Override
      public @NotNull DoubleDatum.Builder.Raw _raw() { return this; }

      @Override
      public @NotNull Val.Builder.Raw asValue() { return value; }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoubleDatum)) return false;
        DoubleDatum that = (DoubleDatum) o;
        return type().equals(that.type()) && val.equals(that._raw().getVal());
      }

      @Override
      public final int hashCode() { return Objects.hash(type(), val); }

    }


    public static abstract class Static<
        MyImmDatum extends DoubleDatum.Imm.Static,
        MyValBuilder extends Val.Builder.Static
        > extends DoubleDatum.Builder
        implements DoubleDatum.Static, PrimitiveDatum.Builder.Static<Double, MyImmDatum> {

      private final @NotNull DoubleDatum.Builder.Raw raw;

      private final @NotNull MyValBuilder value;

      private final @NotNull Function<DoubleDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull DoubleType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull DoubleDatum.Builder.Raw raw,
          @NotNull Function<DoubleDatum.Imm.Raw, MyImmDatum> immDatumConstructor,
          @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> builderValConstructor
      ) {
        super(type);
        // TODO check type equality
        this.raw = raw;
        this.value = builderValConstructor.apply(new Val.Builder.Raw.DatumVal(this));
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
      public @NotNull MyValBuilder asValue() { return value; }

      @Override
      public final int hashCode() { return raw.hashCode(); }

      @Override
      public final boolean equals(Object obj) { return raw.equals(obj); }

    }


  }


}
