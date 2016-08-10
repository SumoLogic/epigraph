/* Created by yegor on 8/3/16. */

package io.epigraph.datum;

import io.epigraph.errors.ErrorValue;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


//@Deprecated
//public abstract class Value<MyDatum extends Datum> {
//
//  public abstract @NotNull DatumType type(); // TODO do we need this method at all? (yes, in modifiable values - for type checking in setData())
//
//  abstract @Nullable MyDatum _getDatum();
//
//  abstract @Nullable ErrorValue _getError();
//
//
//  static abstract class Impl<MyDatum extends Datum> extends Value<MyDatum> {
//
//    private final @NotNull DatumType type;
//
//    public Impl(@NotNull DatumType type) { this.type = type; }
//
//    @Override
//    public @NotNull DatumType type() { return type; }
//
//    @Override
//    abstract @Nullable MyDatum _getDatum();
//
//  }
//
//
//  public static abstract class Imm<MyDatum extends Datum.Imm> extends Value.Impl<MyDatum> implements Immutable {
//
//    public Imm(@NotNull DatumType type) { super(type); }
//
//
//    private static final class NullValue<MyDatum extends Datum.Imm> extends Value.Imm<MyDatum> {
//
//      public NullValue(@NotNull DatumType type) { super(type); } // TODO cache in datum type?
//
//      @Override
//      @Nullable MyDatum _getDatum() { return null; }
//
//      @Override
//      @Nullable ErrorValue _getError() { return null; }
//
//    }
//
//
//    private static final class DatumValue<MyDatum extends Datum.Imm> extends Value.Imm<MyDatum> {
//
//      private final @NotNull MyDatum datum;
//
//      public DatumValue(@NotNull DatumType type, @NotNull MyDatum datum) {
//        // TODO derive type from datum? or do we want to take sub-typed datum?
//        super(type);
//        this.datum = datum;
//      }
//
//      @Override
//      public @NotNull MyDatum _getDatum() { return datum; }
//
//      @Override
//      public @Nullable ErrorValue _getError() { return null; }
//
//    }
//
//
//    private static final class ErrorImmValue<MyDatum extends Datum.Imm> extends Value.Imm<MyDatum> {
//
//      private final @NotNull ErrorValue error;
//
//      public ErrorImmValue(@NotNull DatumType type, @NotNull ErrorValue error) {
//        super(type);
//        this.error = error;
//      }
//
//      @Override
//      public @Nullable MyDatum _getDatum() { return null; }
//
//      @Override
//      public @NotNull ErrorValue _getError() { return error; }
//
//    }
//
//
//  }
//
//
//  public static class Mut<MyDatum extends Datum.Mut> extends Value.Impl<MyDatum> implements Mutable {
//
//    private @Nullable Object datumOrError;
//
//    public Mut(@NotNull DatumType type) {
//      super(type);
//      this.datumOrError = null;
//    }
//
//    @Override
//    @Nullable MyDatum _getDatum() {
//      Object local = datumOrError;
//      return local instanceof ErrorValue ? null : (MyDatum) local;
//    }
//
//    void _setDatum(@Nullable MyDatum datum) { // TODO take Datum and auto-convert (via protected abstract method)?
//      // TODO check datum compatibility vs this.type
//      datumOrError = datum;
//    }
//
//    @Override
//    @Nullable ErrorValue _getError() {
//      Object local = datumOrError;
//      return local instanceof ErrorValue ? (ErrorValue) local : null;
//    }
//
//    void _setError(@NotNull ErrorValue error) { datumOrError = error; }
//
//
//  }
//
//
//}
