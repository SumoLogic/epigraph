/* Created by yegor on 8/5/16. */

package io.epigraph.data;

import io.epigraph.errors.ErrorValue;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface Val {

  @NotNull DatumType type();

  @Nullable Datum getDatum(); // TODO getDatum()?

  @Nullable ErrorValue getError(); // TODO getError()?

  @NotNull Val.Imm toImmutable();

  @NotNull Val.Raw _raw();


  abstract class Impl implements Val {

    private final @NotNull DatumType type;

    protected Impl(@NotNull DatumType type) { this.type = type; }

    @Override
    public @NotNull DatumType type() { return type; }

  }


  interface Raw extends Val {

    @Override
    @NotNull Val.Imm.Raw toImmutable();

  }


  interface Static extends Val {

    @Override
    @Nullable Datum.Static getDatum();

    @Override
    @NotNull Val.Imm.Static toImmutable();

  }


  interface Imm extends Val, Immutable {

    @Override
    @Nullable Datum.Imm getDatum();

    @Override
    @NotNull Val.Imm.Raw _raw();


    abstract class Raw extends Val.Impl implements Val.Imm, Val.Raw {

      protected Raw(DatumType type) { super(type); }

      public static @NotNull Val.Imm.Raw create(@NotNull Val value) {
        ErrorValue error = value.getError();
        return error == null ? create(value.type(), value.getDatum()) : create(value.type(), error);
      }

      public static @NotNull Val.Imm.Raw create(@NotNull DatumType type, @NotNull ErrorValue error) {
        return new Val.Imm.Raw.ErrorVal(type, error);
      }

      public static @NotNull Val.Imm.Raw create(@NotNull DatumType type, @Nullable Datum datum) {
        return datum == null ? new Val.Imm.Raw.NullVal(type) : new Val.Imm.Raw.DatumVal(type, datum.toImmutable());
      }

      @Override
      public final @NotNull Val.Imm.Raw toImmutable() { return this; }

      @Override
      public final @NotNull Val.Imm.Raw _raw() { return this; }


      private static final class NullVal extends Val.Imm.Raw {

        public NullVal(@NotNull DatumType type) { super(type); } // TODO cache in datum type?

        @Override
        public @Nullable Datum.Imm getDatum() { return null; }

        @Override
        public @Nullable ErrorValue getError() { return null; }

      }


      private static final class DatumVal extends Val.Imm.Raw {

        private final @NotNull Datum.Imm datum;

        public DatumVal(@NotNull DatumType type, @NotNull Datum.Imm datum) {
          // TODO derive type from datum? or do we want to take sub-typed datum?
          super(type);
          this.datum = datum; // TODO check datum type is subtype of this.type
        }

        @Override
        public @NotNull Datum.Imm getDatum() { return datum; }

        @Override
        public @Nullable ErrorValue getError() { return null; }

      }


      private static final class ErrorVal extends Val.Imm.Raw {

        private final @NotNull ErrorValue error;

        public ErrorVal(@NotNull DatumType type, @NotNull ErrorValue error) {
          super(type);
          this.error = error;
        }

        @Override
        public @Nullable Datum.Imm getDatum() { return null; }

        @Override
        public @NotNull ErrorValue getError() { return error; }

      }


    }


    interface Static extends Val.Imm, Val.Static { // TODO convert to class?


      @Override
      @Nullable Datum.Imm.Static getDatum();

      @Override
      @NotNull Val.Imm.Static toImmutable();


      abstract class Impl<
          MyImmVal extends Val.Imm.Static,
          MyImmDatum extends Datum.Imm.Static
          > extends Val.Impl implements Val.Imm.Static {

        private final Val.Imm.Raw raw;

        public Impl(@NotNull DatumType type, @NotNull Val.Imm.Raw raw) {
          super(type);
          if (!raw.type().doesExtend(type)) throw new IllegalArgumentException("Incompatible ... TODO");
          this.raw = raw;
        }

        @Override
        public @Nullable MyImmDatum getDatum() { return (MyImmDatum) raw.getDatum(); }

        @Override
        public @Nullable ErrorValue getError() { return raw.getError(); }

        @Override
        public @NotNull MyImmVal toImmutable() { return (MyImmVal) this; }

        @Override
        public @NotNull Val.Imm.Raw _raw() { return raw; }

      }


    }


  }


  abstract class Builder extends Val.Impl implements Mutable {

    protected Builder(@NotNull DatumType type) { super(type); }

    public abstract void setError(@NotNull ErrorValue error);

    @Override
    public abstract @NotNull Val.Builder.Raw _raw();


    public static final class Raw extends Val.Builder implements Val.Raw {

      private @Nullable Object datumOrError = null;

      public Raw(@NotNull DatumType type) { super(type); }

      @Override
      public @Nullable Datum getDatum() {
        Object local = datumOrError;
        return local instanceof ErrorValue ? null : (Datum.Builder) local;
      }

      // TODO take Datum and auto-convert (via protected abstract method)?
      public void setDatum(@Nullable Datum datum) { // TODO setDatum()?
        if (datum != null && datum.type() != type()) throw new IllegalArgumentException("Incompatible ... TODO");
        datumOrError = datum;
      }

      @Override
      public @Nullable ErrorValue getError() {
        Object local = datumOrError;
        return local instanceof ErrorValue ? (ErrorValue) local : null;
      }

      public void setError(@NotNull ErrorValue error) { datumOrError = error; }

      @Override
      public @NotNull Val.Imm.Raw toImmutable() {
        Object local = datumOrError;
        return local instanceof ErrorValue
            ? Val.Imm.Raw.create(type(), (ErrorValue) local)
            : Val.Imm.Raw.create(type(), (Datum.Builder) local);
      }

      @Override
      public @NotNull Val.Builder.Raw _raw() { return this; }

    }


    public static abstract class Static<MyImmVal extends Val.Imm.Static, MyDatum extends Datum.Static>
        extends Val.Builder implements Val.Static {

      private final Val.Builder.Raw raw;

      private final @NotNull Function<Val.Imm.Raw, MyImmVal> immutableConstructor;

      public Static(@NotNull Val.Builder.Raw raw, @NotNull Function<Val.Imm.Raw, MyImmVal> immutableConstructor) {
        super(raw.type());
        // TODO check type equality
        this.raw = raw; // TODO validate raw data is kosher?
        this.immutableConstructor = immutableConstructor;
      }

      @Override
      public @Nullable MyDatum getDatum() {
        @Nullable Datum datum = raw.getDatum();
        assert !(datum instanceof Datum.Raw);
        return (MyDatum) datum;
      }

      public void setDatum(@Nullable MyDatum datum) { raw.setDatum(datum); }

      @Override
      public final @Nullable ErrorValue getError() { return raw.getError(); }

      @Override
      public void setError(@NotNull ErrorValue error) { raw.setError(error); }


      @Override
      final public @NotNull MyImmVal toImmutable() { return immutableConstructor.apply(_raw().toImmutable()); }

      @Override
      public @NotNull Val.Builder.Raw _raw() { return raw; }

    }


  }


  abstract class Mut extends Val.Impl implements Mutable {

    protected Mut(@NotNull DatumType type) { super(type); }

    public abstract void setError(@NotNull ErrorValue error);

    @Override
    public abstract @NotNull Val.Mut.Raw _raw();


    public static final class Raw extends Val.Mut implements Val.Raw {

      private @Nullable Object datumOrError;

      public Raw(@NotNull DatumType type) {
        super(type);
        this.datumOrError = null;
      }

      @Override
      public @Nullable Datum.Mut getDatum() {
        Object local = datumOrError;
        return local instanceof ErrorValue ? null : (Datum.Mut) local;
      }

      // TODO take Datum and auto-convert (via protected abstract method)?
      public void setDatum(@Nullable Datum.Mut datum) { // TODO setDatum()?
        if (datum != null && datum.type() != type()) throw new IllegalArgumentException("Incompatible ... TODO");
        datumOrError = datum;
      }

      @Override
      public @Nullable ErrorValue getError() {
        Object local = datumOrError;
        return local instanceof ErrorValue ? (ErrorValue) local : null;
      }

      public void setError(@NotNull ErrorValue error) { datumOrError = error; }

      @Override
      public @NotNull Val.Imm.Raw toImmutable() {
        Object local = datumOrError;
        return local instanceof ErrorValue
            ? Val.Imm.Raw.create(type(), (ErrorValue) local)
            : Val.Imm.Raw.create(type(), ((Datum.Mut) local));
      }

      @Override
      public @NotNull Val.Mut.Raw _raw() { return this; }

    }


    public static abstract class Static<MyImmVal extends Val.Imm.Static, MyMutDatum extends Datum.Mut.Static>
        extends Val.Mut implements Val.Static {

      private final Val.Mut.Raw raw;

      private final @NotNull Function<Val.Imm.Raw, MyImmVal> immutableConstructor;

      public Static(@NotNull Val.Mut.Raw raw, @NotNull Function<Val.Imm.Raw, MyImmVal> immutableConstructor) {
        super(raw.type());
        // TODO check type equality
        this.raw = raw; // TODO validate raw data is kosher?
        this.immutableConstructor = immutableConstructor;
      }

      @Override
      public @Nullable MyMutDatum getDatum() {
        @Nullable Datum.Mut datum = raw.getDatum();
        assert !(datum instanceof Datum.Raw);
        return (MyMutDatum) datum;
      }

      // TODO take Datum and auto-convert (via protected abstract method)?
      public void setDatum(@Nullable MyMutDatum datum) { raw.setDatum(datum); }

      @Override
      public final @Nullable ErrorValue getError() { return raw.getError(); }

      @Override
      public void setError(@NotNull ErrorValue error) { raw.setError(error); }


      @Override
      final public @NotNull MyImmVal toImmutable() { return immutableConstructor.apply(_raw().toImmutable()); }

      @Override
      public @NotNull Val.Mut.Raw _raw() { return raw; }

    }


  }


}
