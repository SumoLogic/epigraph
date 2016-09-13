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
    @NotNull Map<String, ? extends Data> _fieldsData();

    @Nullable Data _getData(@NotNull Field field);

    @Nullable Val _getValue(@NotNull Field field, @NotNull Tag tag);

    @Nullable Datum _getDatum(@NotNull Field field, @NotNull Tag tag);

    @Nullable ErrorValue _getError(@NotNull Field field, @NotNull Tag tag);

    // TODO @Nullable Datum _getDatum(Field, Tag)
    // TODO @Nullable ErrorValue _getError(Field, Tag)

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
            ? ((RecordDatum.Imm) prototype)._raw()._fieldsData()
            : Unmodifiable
                .map(prototype._raw()._fieldsData().entrySet(), Map.Entry::getKey, me -> me.getValue().toImmutable());
      }

      @Override
      public @NotNull RecordDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull RecordDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Map<String, ? extends Data.Imm> _fieldsData() { return fieldsData; }

      @Override
      public @Nullable Data.Imm _getData(@NotNull Field field) {
        return _fieldsData().get(type().assertReadable(field).name());
      }

      @Override
      public @Nullable Val.Imm _getValue(@NotNull Field field, @NotNull Tag tag) {
        Data.Imm data = _getData(field);
        return data == null ? null : data._raw()._getValue(tag);
      }

      @Override
      public @Nullable Datum _getDatum(@NotNull Field field, @NotNull Tag tag) {
        Data.Imm data = _getData(field);
        return data == null ? null : data._raw()._getDatum(tag);
      }

      @Override
      public @Nullable ErrorValue _getError(@NotNull Field field, @NotNull Tag tag) {
        Data.Imm data = _getData(field);
        return data == null ? null : data._raw()._getError(tag);
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
      public @NotNull Map<String, ? extends Data.Mut> _fieldsData() { return fieldsData; }

      @Override
      public @Nullable Data.Mut _getData(@NotNull Field field) {
        return _fieldsData().get(type().assertReadable(field).name());
      }

      @Override
      public @Nullable Val.Mut _getValue(@NotNull Field field, @NotNull Tag tag) {
        Data.Mut data = _getData(field);
        return data == null ? null : data._raw()._getValue(tag);
      }

      @Override
      public @Nullable Datum _getDatum(@NotNull Field field, @NotNull Tag tag) {
        Data.Mut data = _getData(field);
        return data == null ? null : data._raw()._getDatum(tag);
      }

      @Override
      public @Nullable ErrorValue _getError(@NotNull Field field, @NotNull Tag tag) {
        Data.Mut data = _getData(field);
        return data == null ? null : data._raw()._getError(tag);
      }

      public void _set(@NotNull Field field, @NotNull Tag tag, @Nullable Val value) {
        // FIXME
      }

      // TODO allow Data (auto-convert to Data.Mut)?
      void _setData(@NotNull Field field, @Nullable Data.Mut data) {
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
        Data.Mut data = _getData(field);
        if (data == null) _setData(field, data = field.type.createDataBuilder());
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
