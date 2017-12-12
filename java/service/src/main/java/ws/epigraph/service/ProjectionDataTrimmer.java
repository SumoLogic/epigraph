/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.service;

import ws.epigraph.data.*;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.req.*;
import ws.epigraph.types.Field;
import ws.epigraph.types.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ProjectionDataTrimmer {
  // todo move somewhere else? Generify?
  // todo support recursive data

  public ProjectionDataTrimmer() {}

  public @NotNull Data trimData(@NotNull Data data, @NotNull ReqProjection<?, ?> projection) {
    final @NotNull Data.Raw raw = data._raw();
    final @NotNull Data.Builder.Raw b = data.type().createDataBuilder()._raw();

    ReqProjection<?, ?> normalizedProjection = projection.normalizedForType(data.type());

    for (Map.Entry<String, ReqTagProjectionEntry> entry : normalizedProjection.tagProjections().entrySet()) {
      final String tagName = entry.getKey();
      final Tag tag = (Tag) normalizedProjection.type().tagsMap().get(tagName);

      final @Nullable Val val = raw.getValue(tag);
      if (val != null) {
        final @Nullable ErrorValue error = val.getError();
        if (error != null) b.setError(tag, error);

        final @Nullable Datum datum = val.getDatum();
        if (datum != null) b.setDatum(tag, trimDatum(datum, entry.getValue().modelProjection()));
      }
    }

    return b;
  }

  public @NotNull Datum trimDatum(@NotNull Datum datum, @NotNull ReqModelProjection<?, ?, ?> projection) {
    Datum.Builder.Raw b = trimDatumNoMeta(datum, projection);

    final ReqModelProjection<?, ?, ?> metaProjection = projection.metaProjection();
    if (metaProjection != null) {
      final Datum meta = datum._raw().meta();
      if (meta != null) {
        b.setMeta(trimDatumNoMeta(meta, metaProjection));
      }
    }

    return b;
  }

  private @NotNull Datum.Builder.Raw trimDatumNoMeta(
      @NotNull Datum datum,
      @NotNull ReqModelProjection<?, ?, ?> projection) {

    switch (datum.type().kind()) {
      case RECORD:
        return trimRecordDatum((RecordDatum) datum, (ReqRecordModelProjection) projection);
      case MAP:
        return trimMapDatum((MapDatum) datum, (ReqMapModelProjection) projection);
      case LIST:
        return trimListDatum((ListDatum) datum, (ReqListModelProjection) projection);
      case PRIMITIVE:
        return trimPrimitiveDatum((PrimitiveDatum<?>) datum, (ReqPrimitiveModelProjection) projection);
      case ENUM:
        throw new RuntimeException("Unsupported kind kind: " + datum.type().kind());
      case ENTITY:
        throw new RuntimeException("Unexpected kind kind: " + datum.type().kind());
      default:
        throw new RuntimeException("Unknown kind kind: " + datum.type().kind());
    }
  }

  public @NotNull Datum.Builder.Raw trimRecordDatum(
      @NotNull RecordDatum datum,
      @NotNull ReqRecordModelProjection projection) {
    final @NotNull RecordDatum.Raw raw = datum._raw();
    final @NotNull RecordDatum.Builder.Raw b = datum.type().createBuilder()._raw();

    @NotNull
    Map<String, ReqFieldProjectionEntry> fieldProjections = projection.fieldProjections();

    for (Map.Entry<String, ReqFieldProjectionEntry> entry : fieldProjections.entrySet()) {
      final ReqFieldProjectionEntry fieldProjectionEntry = entry.getValue();
      final Field field = raw.type().fieldsMap().get(entry.getKey());
      assert field != null : raw.type().name().toString() + "." + entry.getKey();
      final @Nullable Data data = raw.getData(field);

      if (data != null) b.setData(field, trimData(data, fieldProjectionEntry.fieldProjection().projection()));
    }

    return b;
  }

  public @NotNull Datum.Builder.Raw trimMapDatum(
      @NotNull MapDatum datum,
      @NotNull ReqMapModelProjection projection) {

    final @NotNull MapDatum.Raw raw = datum._raw();
    final @NotNull MapDatum.Builder.Raw b = datum.type().createBuilder()._raw();

    final @Nullable List<ReqKeyProjection> keyProjections = projection.keys();

    if (keyProjections == null) {
      for (Map.Entry<Datum.Imm, ? extends Data> entry : raw.elements().entrySet()) {
        final Datum.Imm keyValue = entry.getKey();
        final Data data = entry.getValue();

        if (data != null) b.elements().put(keyValue, trimData(data, projection.itemsProjection()));
      }
    } else {
      for (ReqKeyProjection keyProjection : keyProjections) {
        final @NotNull Datum.Imm keyValue = keyProjection.value().toImmutable();
        final @Nullable Data data = raw.elements().get(keyValue);

        if (data != null) b.elements().put(keyValue, trimData(data, projection.itemsProjection()));
      }
    }

    return b;
  }

  public static @NotNull Datum.Builder.Raw trimListDatum(
      @NotNull ListDatum datum,
      @NotNull ReqListModelProjection projection) {

    // nothing to trim.
    // Todo: use toBuilder once available

    if (datum instanceof ListDatum.Builder.Raw)
      return (ListDatum.Builder.Raw) datum;

    final ListDatum.Builder.Raw b = datum.type().createBuilder()._raw();
    b.elements().addAll(datum._raw().elements());
    return b;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static @NotNull Datum.Builder.Raw trimPrimitiveDatum(
      @NotNull PrimitiveDatum<?> datum,
      @NotNull ReqPrimitiveModelProjection projection) {
    // nothing to trim
    // Todo: use toBuilder once available

    if (datum instanceof PrimitiveDatum.Builder.Raw) {
      return (PrimitiveDatum.Builder.Raw) datum;
    }

    PrimitiveDatum<Object> _d = (PrimitiveDatum<Object>) datum;
    return _d.type().createBuilder(datum.getVal())._raw();
  }

//  public static Datum trimEnumDatum(@NotNull EnumDatum datum, @NotNull ReqOutputEnumModelProjection projection) { }
}
