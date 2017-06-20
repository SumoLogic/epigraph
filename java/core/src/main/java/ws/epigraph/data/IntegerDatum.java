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

/* Created by yegor on 8/4/16. */

package ws.epigraph.data;

import org.jetbrains.annotations.Nullable;
import ws.epigraph.types.IntegerType;
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

  @Override
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

      private final @Nullable Datum.Imm meta;

      private final @NotNull Val.Imm.Raw value = new Val.Imm.Raw.DatumVal(this);

      private final int hashCode;

      @Override
      public @Nullable Datum.Imm meta() { return meta; }

      public Raw(@NotNull IntegerDatum.Builder.Raw mutable) {
        super(mutable.type());
        val = mutable.getVal();
        Datum _meta = mutable.meta();
        meta = _meta == null ? null : _meta.toImmutable();
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

        @SuppressWarnings("unchecked")
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

    @Override
    public abstract void setVal(@NotNull Integer val);

    @Override
    public abstract @NotNull IntegerDatum.Builder.Raw _raw();


    public static final class Raw extends IntegerDatum.Builder implements IntegerDatum.Raw, PrimitiveDatum.Builder.Raw<Integer> {

      private @NotNull Integer val;

      private @Nullable Datum meta;

      private final @NotNull Val.Builder.Raw value = new Val.Builder.Raw.DatumVal(this);

      public Raw(@NotNull IntegerType type, @NotNull Integer val) {
        super(type);
        this.val = val;
      }

      @Override
      public @NotNull Integer getVal() { return val; }

      @Override
      public void setVal(@NotNull Integer val) { this.val = val; }

      @Override
      public @Nullable Datum meta() { return meta; }

      @Override
      public @NotNull Datum.@NotNull Builder setMeta(final @Nullable Datum meta) {
        this.meta = type().checkMeta(meta);
        return this;
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


    public abstract static class Static<
        MyImmDatum extends IntegerDatum.Imm.Static,
        MyValBuilder extends Val.Builder.Static
        > extends IntegerDatum.Builder
        implements IntegerDatum.Static, PrimitiveDatum.Builder.Static<Integer, MyImmDatum> {

      private final @NotNull IntegerDatum.Builder.Raw raw;

      private final @NotNull MyValBuilder value;

      private final @NotNull Function<IntegerDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull IntegerType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull IntegerDatum.Builder.Raw raw,
          @NotNull Function<IntegerDatum.Imm.Raw, MyImmDatum> immDatumConstructor,
          @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> builderValConstructor
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
      public @NotNull MyValBuilder asValue() { return value; }

      @Override
      public final int hashCode() { return raw.hashCode(); }

      @Override
      public final boolean equals(Object obj) { return raw.equals(obj); }

    }


  }


}
