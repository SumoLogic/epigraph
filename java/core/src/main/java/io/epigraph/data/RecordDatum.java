/* Created by yegor on 8/3/16. */

package io.epigraph.data;

import io.epigraph.errors.ErrorValue;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import io.epigraph.types.Type.Tag;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public interface RecordDatum extends Datum {

  @Override
  @NotNull RecordDatum.Imm toImmutable();

  @Override
  @NotNull RecordDatum.Raw _raw();


  abstract class Impl extends Datum.Impl<RecordType> implements RecordDatum {

    protected Impl(RecordType type) { super(type); }

  }


  interface Raw extends RecordDatum, Datum.Raw {

    @Override
    @NotNull RecordDatum.Imm.Raw toImmutable();

    /**
     * @return Unmodifiable mapping of field names to their data. The data could be modifiable.
     */
    @NotNull Map<String, ? extends Data> fieldsData();

    @Nullable Data getData(@NotNull Field field);

    @Nullable Val getValue(@NotNull Field field, @NotNull Tag tag);

    @Nullable Datum getDatum(@NotNull Field field, @NotNull Tag tag);

    @Nullable ErrorValue getError(@NotNull Field field, @NotNull Tag tag);

  }


  interface Static extends RecordDatum, Datum.Static {

    @Override
    @NotNull RecordDatum.Imm.Static toImmutable();

  }


  interface Imm extends RecordDatum, Datum.Imm {

    @Override
    @NotNull RecordDatum.Imm.Raw _raw();


    final class Raw extends RecordDatum.Impl implements RecordDatum.Imm, RecordDatum.Raw, Datum.Imm.Raw {

      private final Map<String, ? extends Data.Imm> fieldsData;

      // TODO take RecordDatum.Mut only? make private and use static method to re-use immutables?
      public Raw(RecordType type, RecordDatum prototype) {
        super(type);
        fieldsData = prototype instanceof RecordDatum.Imm
            ? ((RecordDatum.Imm) prototype)._raw().fieldsData()
            : Unmodifiable
                .map(prototype._raw().fieldsData().entrySet(), Map.Entry::getKey, me -> me.getValue().toImmutable());
      }

      @Override
      public @NotNull RecordDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull RecordDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Map<String, ? extends Data.Imm> fieldsData() { return fieldsData; }

      @Override
      public @Nullable Data.Imm getData(@NotNull Field field) {
        return fieldsData().get(type().assertReadable(field).name());
      }

      @Override
      public @Nullable Val.Imm getValue(@NotNull Field field, @NotNull Tag tag) {
        Data.Imm data = getData(field);
        return data == null ? null : data._raw().getValue(tag);
      }

      @Override
      public @Nullable Datum getDatum(@NotNull Field field, @NotNull Tag tag) {
        Data.Imm data = getData(field);
        return data == null ? null : data._raw().getDatum(tag);
      }

      @Override
      public @Nullable ErrorValue getError(@NotNull Field field, @NotNull Tag tag) {
        Data.Imm data = getData(field);
        return data == null ? null : data._raw().getError(tag);
      }

    }


    // base for generated immutable record interfaces
    interface Static extends RecordDatum.Imm, RecordDatum.Static, Datum.Imm.Static {


      // base for generated immutable record impl classes
      abstract class Impl<MyImmDatum extends RecordDatum.Imm.Static> extends RecordDatum.Impl
          implements RecordDatum.Imm.Static {

        private final RecordDatum.Imm.Raw raw;

        protected Impl(@NotNull RecordType type, @NotNull RecordDatum.Imm.Raw raw) {
          super(type);
          if (!type.isAssignableFrom(raw.type())) throw new IllegalArgumentException(
              "Incompatible raw and static types (TODO details)"
          );
          this.raw = raw; // TODO check raw data internals is kosher? or trust protected invoker?
        }

        @Override
        public @NotNull MyImmDatum toImmutable() { return (MyImmDatum) this; } // TODO this could be violated - make abstract?..

        @Override
        public @NotNull RecordDatum.Imm.Raw _raw() { return raw; }

      }


    }


  }


  abstract class Builder extends RecordDatum.Impl implements Datum.Builder {

    protected Builder(@NotNull RecordType type) { super(type); }

    @Override
    public abstract @NotNull RecordDatum.Builder.Raw _raw();


    public static final class Raw extends RecordDatum.Builder implements RecordDatum.Raw, Datum.Builder.Raw {

      private final Map<String, Data.Builder> fieldsData = new HashMap<>();

      public Raw(@NotNull RecordType type) { super(type); }

      @Override
      public @NotNull RecordDatum.Imm.Raw toImmutable() { return new RecordDatum.Imm.Raw(type(), this); }

      @Override
      public @NotNull RecordDatum.Builder.Raw _raw() { return this; }

      @Override
      public @NotNull Map<String, ? extends Data.Builder> fieldsData() { return fieldsData; }

      @Override
      public @Nullable Data.Builder getData(@NotNull Field field) {
        return fieldsData().get(type().assertReadable(field).name());
      }

      @Override
      public @Nullable Val getValue(@NotNull Field field, @NotNull Tag tag) {
        Data.Builder data = getData(field);
        return data == null ? null : data._raw().getValue(tag);
      }

      @Override
      public @Nullable Datum getDatum(@NotNull Field field, @NotNull Tag tag) {
        Data.Builder data = getData(field);
        return data == null ? null : data._raw().getDatum(tag);
      }

      @Override
      public @Nullable ErrorValue getError(@NotNull Field field, @NotNull Tag tag) {
        Data.Builder data = getData(field);
        return data == null ? null : data._raw().getError(tag);
      }

      public void set(@NotNull Field field, @NotNull Tag tag, @Nullable Val value) {
        // FIXME
      }

      // TODO allow Data (auto-convert to Data.Builder)?
      public void setData(@NotNull Field field, @Nullable Data.Builder data) {
        if (data == null) {
          fieldsData.remove(type().assertWritable(field).name());
        } else {
          // TODO check data is compatible with effective field
          fieldsData.put(
              type().assertWritable(field).name(),
              data
          ); // TODO parameterize with mut data type (final raw or static)?
        }
      }

      public @NotNull Data.Builder getOrCreateFieldData(@NotNull Field field) {
        Data.Builder data = getData(field);
        if (data == null) setData(field, data = field.type.createDataBuilder());
        return data;
      }

    }


    // base for generated mutable record impl classes
    public static abstract class Static<MyImmDatum extends RecordDatum.Imm.Static> extends RecordDatum.Builder
        implements RecordDatum.Static, Datum.Builder.Static<MyImmDatum> {

      private final RecordDatum.Builder.Raw raw;

      private final @NotNull Function<RecordDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull RecordType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull RecordDatum.Builder.Raw raw,
          @NotNull Function<RecordDatum.Imm.Raw, MyImmDatum> immDatumConstructor
      ) {
        super(type);
        if (raw.type() != type) throw new IllegalArgumentException("Raw type doesn't match static type (TODO details)");
        this.raw = raw;
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull RecordDatum.Builder.Raw _raw() { return raw; }

    }


  }

  abstract class Mut extends RecordDatum.Impl implements Datum.Mut {

    protected Mut(@NotNull RecordType type) { super(type); }

    @Override
    public abstract @NotNull RecordDatum.Mut.Raw _raw();


    public static final class Raw extends RecordDatum.Mut implements RecordDatum.Raw, Datum.Mut.Raw {

      private final Map<String, Data.Mut> fieldsData = new HashMap<>();

      public Raw(@NotNull RecordType type) { super(type); }

      @Override
      public @NotNull RecordDatum.Imm.Raw toImmutable() { return new RecordDatum.Imm.Raw(type(), this); }

      @Override
      public @NotNull RecordDatum.Mut.Raw _raw() { return this; }

      @Override
      public @NotNull Map<String, ? extends Data.Mut> fieldsData() { return fieldsData; }

      @Override
      public @Nullable Data.Mut getData(@NotNull Field field) {
        return fieldsData().get(type().assertReadable(field).name());
      }

      @Override
      public @Nullable Val.Mut getValue(@NotNull Field field, @NotNull Tag tag) {
        Data.Mut data = getData(field);
        return data == null ? null : data._raw().getValue(tag);
      }

      @Override
      public @Nullable Datum getDatum(@NotNull Field field, @NotNull Tag tag) {
        Data.Mut data = getData(field);
        return data == null ? null : data._raw().getDatum(tag);
      }

      @Override
      public @Nullable ErrorValue getError(@NotNull Field field, @NotNull Tag tag) {
        Data.Mut data = getData(field);
        return data == null ? null : data._raw().getError(tag);
      }

      public void set(@NotNull Field field, @NotNull Tag tag, @Nullable Val value) {
        // FIXME
      }

      // TODO allow Data (auto-convert to Data.Mut)?
      public void setData(@NotNull Field field, @Nullable Data.Mut data) {
        if (data == null) {
          fieldsData.remove(type().assertWritable(field).name());
        } else {
          // TODO check data is compatible with effective field
          fieldsData.put(
              type().assertWritable(field).name(),
              data
          ); // TODO parameterize with mut data type (final raw or static)?
        }
      }

      public @NotNull Data.Mut getOrCreateFieldData(@NotNull Field field) {
        Data.Mut data = getData(field);
        if (data == null) setData(field, data = field.type.createMutableData());
        return data;
      }
    }


    // base for generated mutable record impl classes
    public static abstract class Static<MyImmDatum extends RecordDatum.Imm.Static> extends RecordDatum.Mut
        implements RecordDatum.Static, Datum.Mut.Static<MyImmDatum> {

      private final RecordDatum.Mut.Raw raw;

      private final @NotNull Function<RecordDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull RecordType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull RecordDatum.Mut.Raw raw,
          @NotNull Function<RecordDatum.Imm.Raw, MyImmDatum> immDatumConstructor
      ) {
        super(type);
        if (raw.type() != type) throw new IllegalArgumentException("Raw type doesn't match static type (TODO details)");
        this.raw = raw;
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull RecordDatum.Mut.Raw _raw() { return raw; }

    }


  }


}
