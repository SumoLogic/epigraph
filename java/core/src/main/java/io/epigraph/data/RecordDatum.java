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

      private final @NotNull Val.Imm.Raw value = new Val.Imm.Raw.DatumVal(this);

      public Raw(@NotNull RecordDatum.Builder.Raw builder) {
        super(builder.type());
        fieldsData = Unmodifiable.map(builder.fieldsData(), k->k, Data::toImmutable);
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

      @Override
      public @NotNull Val.Imm.Raw asValue() { return value; }

    }


    // base for generated immutable record interfaces
    interface Static extends RecordDatum.Imm, RecordDatum.Static, Datum.Imm.Static {


      // base for generated immutable record impl classes
      abstract class Impl<MyImmDatum extends RecordDatum.Imm.Static, MyImmVal extends Val.Imm.Static>
          extends RecordDatum.Impl implements RecordDatum.Imm.Static {

        private final RecordDatum.Imm.Raw raw;

        private final @NotNull MyImmVal value;

        protected Impl(
            @NotNull RecordType type,
            @NotNull RecordDatum.Imm.Raw raw,
            @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor
        ) {
          super(type);
          if (!type.isAssignableFrom(raw.type())) throw new IllegalArgumentException(
              "Incompatible raw and static types (TODO details)"
          );
          this.raw = raw; // TODO check raw data internals is kosher? or trust protected invoker?
          this.value = immValConstructor.apply(new Val.Imm.Raw.DatumVal(this));
        }

        @Override
        public @NotNull MyImmDatum toImmutable() { return (MyImmDatum) this; } // TODO this could be violated - make abstract?..

        @Override
        public @NotNull RecordDatum.Imm.Raw _raw() { return raw; }

        @Override
        public @NotNull MyImmVal asValue() { return value; }

      }


    }


  }


  abstract class Builder extends RecordDatum.Impl implements Datum.Builder {

    protected Builder(@NotNull RecordType type) { super(type); }

    @Override
    public abstract @NotNull RecordDatum.Builder.Raw _raw();


    public static final class Raw extends RecordDatum.Builder implements RecordDatum.Raw, Datum.Builder.Raw {

      private final Map<String, Data> fieldsData = new HashMap<>();

      private final @NotNull Val.Builder.Raw value = new Val.Builder.Raw.DatumVal(this);

      public Raw(@NotNull RecordType type) { super(type); }

      @Override
      public @NotNull RecordDatum.Imm.Raw toImmutable() { return new RecordDatum.Imm.Raw(this); }

      @Override
      public @NotNull RecordDatum.Builder.Raw _raw() { return this; }

      @Override
      public @NotNull Val.Builder.Raw asValue() { return value; }

      @Override // TODO wrap into type-checking write-through map
      public @NotNull Map<String, ? extends Data> fieldsData() { return fieldsData; }

      @Override
      public @Nullable Data getData(@NotNull Field field) {
        return fieldsData().get(type().assertReadable(field).name());
      }

      @Override
      public @Nullable Val getValue(@NotNull Field field, @NotNull Tag tag) {
        Data data = getData(field);
        return data == null ? null : data._raw().getValue(tag);
      }

      @Override
      public @Nullable Datum getDatum(@NotNull Field field, @NotNull Tag tag) {
        Data data = getData(field);
        return data == null ? null : data._raw().getDatum(tag);
      }

      @Override
      public @Nullable ErrorValue getError(@NotNull Field field, @NotNull Tag tag) {
        Data data = getData(field);
        return data == null ? null : data._raw().getError(tag);
      }

      public void set(@NotNull Field field, @NotNull Tag tag, @Nullable Val value) {
        // FIXME
      }

      public void setData(@NotNull Field field, @Nullable Data data) {
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

      public @NotNull Data getOrCreateFieldData(@NotNull Field field) {
        Data data = getData(field);
        if (data == null) setData(field, data = field.type.createDataBuilder());
        return data;
      }

    }


    // base for generated mutable record impl classes
    public static abstract class Static<
        MyImmDatum extends RecordDatum.Imm.Static,
        MyBuilderVal extends Val.Builder.Static
        > extends RecordDatum.Builder implements RecordDatum.Static, Datum.Builder.Static<MyImmDatum> {

      private final RecordDatum.Builder.Raw raw;

      private final @NotNull MyBuilderVal value;

      private final @NotNull Function<RecordDatum.Imm.@NotNull Raw, @NotNull MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull RecordType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull RecordDatum.Builder.Raw raw,
          @NotNull Function<RecordDatum.Imm.@NotNull Raw, @NotNull MyImmDatum> immDatumConstructor,
          @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyBuilderVal> builderValConstructor
      ) {
        super(type);
        if (raw.type() != type) throw new IllegalArgumentException("Raw type doesn't match static type (TODO details)");
        this.raw = raw;
        this.value = builderValConstructor.apply(new Val.Builder.Raw.DatumVal(this));
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull RecordDatum.Builder.Raw _raw() { return raw; }

      @Override
      public @NotNull MyBuilderVal asValue() { return value; }

    }


  }

//  abstract class Mut extends RecordDatum.Impl implements Datum.Mut {
//
//    protected Mut(@NotNull RecordType type) { super(type); }
//
//    @Override
//    public abstract @NotNull RecordDatum.Mut.Raw _raw();
//
//
//    public static final class Raw extends RecordDatum.Mut implements RecordDatum.Raw, Datum.Mut.Raw {
//
//      private final Map<String, Data.Mut> fieldsData = new HashMap<>();
//
//      public Raw(@NotNull RecordType type) { super(type); }
//
//      @Override
//      public @NotNull RecordDatum.Imm.Raw toImmutable() { return new RecordDatum.Imm.Raw(type(), this); }
//
//      @Override
//      public @NotNull RecordDatum.Mut.Raw _raw() { return this; }
//
//      @Override
//      public @NotNull Map<String, ? extends Data.Mut> fieldsData() { return fieldsData; }
//
//      @Override
//      public @Nullable Data.Mut getData(@NotNull Field field) {
//        return fieldsData().get(type().assertReadable(field).name());
//      }
//
//      @Override
//      public @Nullable Val.Mut getValue(@NotNull Field field, @NotNull Tag tag) {
//        Data.Mut data = getData(field);
//        return data == null ? null : data._raw().getValue(tag);
//      }
//
//      @Override
//      public @Nullable Datum getDatum(@NotNull Field field, @NotNull Tag tag) {
//        Data.Mut data = getData(field);
//        return data == null ? null : data._raw().getDatum(tag);
//      }
//
//      @Override
//      public @Nullable ErrorValue getError(@NotNull Field field, @NotNull Tag tag) {
//        Data.Mut data = getData(field);
//        return data == null ? null : data._raw().getError(tag);
//      }
//
//      public void set(@NotNull Field field, @NotNull Tag tag, @Nullable Val value) {
//        // FIXME
//      }
//
//      // TODO allow Data (auto-convert to Data.Mut)?
//      public void setData(@NotNull Field field, @Nullable Data.Mut data) {
//        if (data == null) {
//          fieldsData.remove(type().assertWritable(field).name());
//        } else {
//          // TODO check data is compatible with effective field
//          fieldsData.put(
//              type().assertWritable(field).name(),
//              data
//          ); // TODO parameterize with mut data type (final raw or static)?
//        }
//      }
//
//      public @NotNull Data.Mut getOrCreateFieldData(@NotNull Field field) {
//        Data.Mut data = getData(field);
//        if (data == null) setData(field, data = field.type.createMutableData());
//        return data;
//      }
//    }
//
//
//    // base for generated mutable record impl classes
//    public static abstract class Static<MyImmDatum extends RecordDatum.Imm.Static> extends RecordDatum.Mut
//        implements RecordDatum.Static, Datum.Mut.Static<MyImmDatum> {
//
//      private final RecordDatum.Mut.Raw raw;
//
//      private final @NotNull Function<RecordDatum.Imm.Raw, MyImmDatum> immDatumConstructor;
//
//      protected Static(
//          @NotNull RecordType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
//          @NotNull RecordDatum.Mut.Raw raw,
//          @NotNull Function<RecordDatum.Imm.Raw, MyImmDatum> immDatumConstructor
//      ) {
//        super(type);
//        if (raw.type() != type) throw new IllegalArgumentException("Raw type doesn't match static type (TODO details)");
//        this.raw = raw;
//        this.immDatumConstructor = immDatumConstructor;
//      }
//
//      @Override
//      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }
//
//      @Override
//      public final @NotNull RecordDatum.Mut.Raw _raw() { return raw; }
//
//    }
//
//
//  }


}
