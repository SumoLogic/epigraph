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

/* Created by yegor on 8/3/16. */

package ws.epigraph.data;

import ws.epigraph.errors.ErrorValue;
import ws.epigraph.types.RecordType;
import ws.epigraph.types.Field;
import ws.epigraph.types.Type.Tag;
import ws.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


public interface RecordDatum extends Datum {

  @Override
  @NotNull RecordType type();

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
    @NotNull Map<@NotNull String, @NotNull ? extends Data> fieldsData();

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

      private final Map<@NotNull String, @NotNull ? extends Data.Imm> fieldsData;

      private final @Nullable Datum.Imm meta;

      private final @NotNull Val.Imm.Raw value = new Val.Imm.Raw.DatumVal(this);

      private final int hashCode;

      public Raw(@NotNull RecordDatum.Builder.Raw builder) {
        super(builder.type());
        fieldsData = Unmodifiable.map(builder.fieldsData(), k -> k, Data::toImmutable);
        Datum _meta = builder.meta();
        meta = _meta == null ? null : _meta.toImmutable();
        hashCode = Objects.hash(type(), fieldsData);
      }

      @Override
      public @Nullable Datum.Imm meta() { return meta; }

      @Override
      public @NotNull RecordDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull RecordDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Map<@NotNull String, @NotNull ? extends Data.Imm> fieldsData() { return fieldsData; }

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

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecordDatum)) return false;
        if (o instanceof Immutable && hashCode != o.hashCode()) return false;
        RecordDatum that = (RecordDatum) o;
        return type().equals(that.type()) && fieldsData.equals(that._raw().fieldsData());
      }

      @Override
      public final int hashCode() { return hashCode; }

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

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull MyImmDatum toImmutable() { return (MyImmDatum) this; } // TODO this could be violated - make abstract?..

        @Override
        public final @NotNull RecordDatum.Imm.Raw _raw() { return raw; }

        @Override
        public @NotNull MyImmVal asValue() { return value; }

        @Override
        public final int hashCode() { return raw.hashCode(); }

        @Override
        public final boolean equals(Object obj) { return raw.equals(obj); }

      }


    }


  }


  abstract class Builder extends RecordDatum.Impl implements Datum.Builder {

    protected Builder(@NotNull RecordType type) { super(type); }

    @Override
    public abstract @NotNull RecordDatum.Builder.Raw _raw();


    public static final class Raw extends RecordDatum.Builder implements RecordDatum.Raw, Datum.Builder.Raw {

      private final Map<@NotNull String, @NotNull Data> fieldsData = new HashMap<>();

      private final Map<@NotNull String, @NotNull ? extends Data> unmodifiableFieldsData = Unmodifiable.map(fieldsData);

      private @Nullable Datum meta;

      private final @NotNull Val.Builder.Raw value = new Val.Builder.Raw.DatumVal(this);

      public Raw(@NotNull RecordType type) { super(type); }

      private Raw(@NotNull RecordType type, @NotNull RecordDatum.Raw prototype) { // wip/experimental
        this(type.checkAssignable(prototype.type()));
        for (Map.Entry<String, ? extends Data> fieldEntry : prototype.fieldsData().entrySet()) {
          String fieldName = fieldEntry.getKey();
          if (type.fieldsMap().containsKey(fieldName)) fieldsData.put(fieldName, fieldEntry.getValue());
        }
      }

      @Override
      public @Nullable Datum meta() { return meta; }

      @Override
      public @NotNull Datum.@NotNull Builder setMeta(final @Nullable Datum meta) {
        this.meta = type().checkMeta(meta);
        return this;
      }

      @Override
      public @NotNull RecordDatum.Imm.Raw toImmutable() { return new RecordDatum.Imm.Raw(this); }

      @Override
      public @NotNull RecordDatum.Builder.Raw _raw() { return this; }

      @Override
      public @NotNull Val.Builder.Raw asValue() { return value; }

      @Override
      public @NotNull Map<@NotNull String, @NotNull ? extends Data> fieldsData() { return unmodifiableFieldsData; }

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

      // TODO parameterize with mut data type (final raw or static)?
      public void setData(@NotNull Field field, @Nullable Data data) {
        if (data == null) {
          fieldsData.remove(type().assertWritable(field).name());
        } else {
          fieldsData.put(type().assertWritable(field).name(), field.dataType().checkAssignable(data));
        }
      }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecordDatum)) return false;
        RecordDatum that = (RecordDatum) o;
        return type().equals(that.type()) && fieldsData.equals(that._raw().fieldsData());
      }

      @Override
      public final int hashCode() { return Objects.hash(type(), fieldsData); }

    }


    // base for generated mutable record impl classes
    public abstract static class Static<
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

      @Override
      public final int hashCode() { return raw.hashCode(); }

      @Override
      public final boolean equals(Object obj) { return raw.equals(obj); }

    }


  }


}
