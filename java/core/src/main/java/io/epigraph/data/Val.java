/* Created by yegor on 8/5/16. */

package io.epigraph.data;

import io.epigraph.errors.ErrorValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;


public interface Val { // TODO rename to TagEntry?

  @Nullable Datum getDatum();

  @Nullable ErrorValue getError(); // TODO isError()?

  @NotNull Val.Imm toImmutable();

  @NotNull Val.Raw _raw();


  interface Raw extends Val {

    @Override
    @NotNull Val.Imm.Raw toImmutable();

    @Override
    @NotNull Val.Raw _raw();

  }


  interface Static extends Val {

    @Override
    @Nullable Datum.Static getDatum();

    @Override
    @NotNull Val.Imm.Static toImmutable();


    abstract class Impl<MyRaw extends Val.Raw, MyImmVal extends Val.Imm.Static, MyDatum extends Datum.Static>
        implements Val.Static {

      protected final MyRaw raw;

      public Impl(@NotNull MyRaw raw) { this.raw = raw; }

      @Override
      public final @Nullable MyDatum getDatum() { return (MyDatum) raw.getDatum(); }

      @Override
      public final @Nullable ErrorValue getError() { return raw.getError(); }


      @Override
      public abstract @NotNull MyImmVal toImmutable();

      @Override
      public final @NotNull MyRaw _raw() { return raw; }

      @Override
      public final int hashCode() { return raw.hashCode(); }

      @Override
      public final boolean equals(Object obj) { return raw.equals(obj); }

    }


  }


  interface Imm extends Val, Immutable {

    @Override
    @Nullable Datum.Imm getDatum();

    @Override
    @NotNull Val.Imm.Raw _raw();


    abstract class Raw implements Val.Imm, Val.Raw {

      public static Val.Imm.Raw create(@Nullable ErrorValue errorOrNull) {
        return errorOrNull == null ? NullVal.instance : new ErrorVal(errorOrNull);
      }

      @Override
      public @NotNull Val.Imm.Raw toImmutable() { return this; }

      @Override
      public final @NotNull Val.Imm.Raw _raw() { return this; }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Val)) return false;
        if (o instanceof Immutable && hashCode() != o.hashCode()) return false;
        Val that = (Val) o;
        return Objects.equals(getDatum(), that.getDatum()) && Objects.equals(getError(), that.getError());
      }


      static final class DatumVal extends Val.Imm.Raw {

        private final @NotNull Datum.Imm datum;

        public DatumVal(@NotNull Datum.Imm datum) { this.datum = datum; }

        @Override
        public @NotNull Datum.Imm getDatum() { return datum; }

        @Override
        public @Nullable ErrorValue getError() { return null; }

        @Override
        public int hashCode() { return datum.hashCode(); }

      }


      static final class NullVal extends Val.Imm.Raw {

        public static @NotNull NullVal instance = new NullVal();

        private NullVal() {}

        @Override
        public @Nullable Datum.Imm getDatum() { return null; }

        @Override
        public @Nullable ErrorValue getError() { return null; }

        @Override
        public int hashCode() { return 1337; }

      }


      static final class ErrorVal extends Val.Imm.Raw {

        private final @NotNull ErrorValue error;

        public ErrorVal(@NotNull ErrorValue error) { this.error = error; }

        @Override
        public @Nullable Datum.Imm getDatum() { return null; }

        @Override
        public @NotNull ErrorValue getError() { return error; }

        @Override
        public int hashCode() { return error.hashCode(); }

      }


    }


    interface Static extends Val.Imm, Val.Static {

      @Override
      @Nullable Datum.Imm.Static getDatum();

      @Override
      @NotNull Val.Imm.Static toImmutable();


      abstract class Impl<MyImmVal extends Val.Imm.Static, MyImmDatum extends Datum.Imm.Static>
          extends Val.Static.Impl<Val.Imm.Raw, MyImmVal, MyImmDatum> implements Val.Imm.Static {

        public Impl(@NotNull Val.Imm.Raw raw) { super(raw); }

        @Override
        public @NotNull MyImmVal toImmutable() { return (MyImmVal) this; }

      }


    }


  }


  interface Builder extends Val, Mutable {


    abstract class Raw implements Val.Builder, Val.Raw {

      @Override
      public @NotNull Val.Builder.Raw _raw() { return this; }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Val)) return false;
        Val that = (Val) o;
        return Objects.equals(getDatum(), that.getDatum()) && Objects.equals(getError(), that.getError());
      }


      static final class DatumVal extends Val.Builder.Raw {

        private final @NotNull Datum.Builder datum;

        public DatumVal(@NotNull Datum.Builder datum) { this.datum = datum; }

        @Override
        public @NotNull Datum.Builder getDatum() { return datum; }

        @Override
        public @Nullable ErrorValue getError() { return null; }

        @Override
        public @NotNull Val.Imm.Raw toImmutable() { return new Val.Imm.Raw.DatumVal(datum.toImmutable()); }

        @Override
        public int hashCode() { return datum.hashCode(); }

      }


      static final class NullVal extends Val.Builder.Raw {

        @Override
        public @Nullable Datum.Imm getDatum() { return null; }

        @Override
        public @Nullable ErrorValue getError() { return null; }

        @Override
        public @NotNull Val.Imm.Raw toImmutable() { return Val.Imm.Raw.NullVal.instance; }

        @Override
        public int hashCode() { return 1337; }

      }


      static final class ErrorVal extends Val.Builder.Raw {

        private final @NotNull ErrorValue error;

        public ErrorVal(@NotNull ErrorValue error) { this.error = error; }

        @Override
        public @Nullable Datum.Imm getDatum() { return null; }

        @Override
        public @NotNull ErrorValue getError() { return error; }

        @Override
        public @NotNull Val.Imm.Raw toImmutable() { return new Val.Imm.Raw.ErrorVal(error); }

        @Override
        public int hashCode() { return error.hashCode(); }

      }


    }


    abstract class Static<MyImmVal extends Val.Imm.Static, MyDatumBuilder extends Datum.Builder.Static>
        extends Val.Static.Impl<Val.Builder.Raw, MyImmVal, MyDatumBuilder>
        implements Val.Builder, Val.Static {

      private final @NotNull Function<Val.Imm.Raw, MyImmVal> immutableConstructor;

      public Static(@NotNull Val.Builder.Raw raw, @NotNull Function<Val.Imm.Raw, MyImmVal> immutableConstructor) {
        super(raw);
        this.immutableConstructor = immutableConstructor;
      }

      @Override
      public final @NotNull MyImmVal toImmutable() { return immutableConstructor.apply(raw.toImmutable()); }

    }


  }


}
