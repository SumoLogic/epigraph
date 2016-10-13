package io.epigraph.service;

import io.epigraph.data.*;
import io.epigraph.errors.ErrorValue;
import io.epigraph.projections.req.output.*;
import io.epigraph.types.RecordType;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionDataTrimmer { // todo move somewhere else?

  @NotNull
  public static Data trimData(@NotNull Data data, @NotNull ReqOutputVarProjection projection) {
    @NotNull final Data.Raw raw = data._raw();
    @NotNull final Data.Builder.Raw b = data.type().createDataBuilder()._raw();

    for (Map.Entry<String, ReqOutputTagProjection> entry : projection.tagProjections().entrySet()) {
      final String tagName = entry.getKey();
      final Type.Tag tag = projection.type().tagsMap().get(tagName);

      @Nullable final Val val = raw.getValue(tag);
      if (val != null) {
        @Nullable final ErrorValue error = val.getError();
        if (error != null) b.setError(tag, error);

        @Nullable final Datum datum = val.getDatum();
        if (datum != null) b.setDatum(tag, trimDatum(datum, entry.getValue().projection()));
      }
    }

    // todo deal with tails

    return b;
  }

  @NotNull
  public static Datum trimDatum(@NotNull Datum datum, @NotNull ReqOutputModelProjection<?> projection) {
    switch (datum.type().kind()) {
      case RECORD:
        return trimRecordDatum((RecordDatum) datum, (ReqOutputRecordModelProjection) projection);
      case MAP:
        return trimMapDatum((MapDatum) datum, (ReqOutputMapModelProjection) projection);
      case LIST:
        return trimListDatum((ListDatum) datum, (ReqOutputListModelProjection) projection);
      case PRIMITIVE:
        return trimPrimitiveDatum((PrimitiveDatum) datum, (ReqOutputPrimitiveModelProjection) projection);
      case ENUM:
        throw new RuntimeException("Unsupported type kind: " + datum.type().kind());
      case UNION:
        throw new RuntimeException("Unexpected type kind: " + datum.type().kind());
      default:
        throw new RuntimeException("Unknown type kind: " + datum.type().kind());
    }
  }

  @NotNull
  public static Datum trimRecordDatum(@NotNull RecordDatum datum, @NotNull ReqOutputRecordModelProjection projection) {
    @NotNull final RecordDatum.Raw raw = datum._raw();
    @NotNull final RecordDatum.Builder.Raw b = datum.type().createBuilder()._raw();

    @Nullable
    LinkedHashMap<String, ReqOutputFieldProjection> fieldProjections = projection.fieldProjections();

    if (fieldProjections != null) {
      for (Map.Entry<String, ReqOutputFieldProjection> entry : fieldProjections.entrySet()) {
        final String fieldName = entry.getKey();
        final RecordType.Field field = projection.model().fieldsMap().get(fieldName);
        @Nullable final Data data = raw.getData(field);

        if (data != null) b.setData(field, trimData(data, entry.getValue().projection()));
      }
    }

    return b;
  }

  @NotNull
  public static Datum trimMapDatum(@NotNull MapDatum datum, @NotNull ReqOutputMapModelProjection projection) {

    @NotNull final MapDatum.Raw raw = datum._raw();
    @NotNull final MapDatum.Builder.Raw b = datum.type().createBuilder()._raw();

    @Nullable final List<ReqOutputKeyProjection> keyProjections = projection.keys();

    if (keyProjections != null) {
      for (ReqOutputKeyProjection keyProjection : keyProjections) {
        @NotNull final Datum.Imm keyValue = keyProjection.value().toImmutable();
        @Nullable final Data data = raw.elements().get(keyValue);

        if (data != null) b.elements().put(keyValue, trimData(data, projection.itemsProjection()));
      }
    } else {
      for (Map.Entry<Datum.Imm, ? extends Data> entry : raw.elements().entrySet()) {
        final Datum.Imm keyValue = entry.getKey();
        final Data data = entry.getValue();

        if (data != null) b.elements().put(keyValue, trimData(data, projection.itemsProjection()));
      }
    }

    return b;
  }

  @NotNull
  public static Datum trimListDatum(@NotNull ListDatum datum, @NotNull ReqOutputListModelProjection projection) {
    // nothing to trim
    return datum;
  }

  @NotNull
  public static Datum trimPrimitiveDatum(@NotNull PrimitiveDatum datum,
                                         @NotNull ReqOutputPrimitiveModelProjection projection) {
    // nothing to trim
    return datum;
  }

//  public static Datum trimEnumDatum(@NotNull EnumDatum datum, @NotNull ReqOutputEnumModelProjection projection) { }
}
