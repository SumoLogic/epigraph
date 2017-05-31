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

package ws.epigraph.data.pruning;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.data.ListDatum;
import ws.epigraph.data.MapDatum;
import ws.epigraph.data.RecordDatum;
import ws.epigraph.data.Val;
import ws.epigraph.data.traversal.DataTraversalContext;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjectionEntry;
import ws.epigraph.projections.req.output.ReqOutputListModelProjection;
import ws.epigraph.projections.req.output.ReqOutputMapModelProjection;
import ws.epigraph.projections.req.output.ReqOutputModelProjection;
import ws.epigraph.projections.req.output.ReqOutputRecordModelProjection;
import ws.epigraph.projections.req.output.ReqOutputTagProjectionEntry;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Field;
import ws.epigraph.types.Tag;
import ws.epigraph.types.TypeKind;
import ws.epigraph.util.HttpStatusCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 * @see <a href="https://github.com/SumoLogic/epigraph/wiki/required#request-projections">required flag for request projections</a>
 */
public class ReqOutputRequiredDataPruner {
  // todo more tests

  private final DataTraversalContext context = new DataTraversalContext();

  public @Nullable DataPruningResult pruneData(@NotNull Data data, @NotNull ReqOutputVarProjection projection) {

    projection = projection.normalizedForType(data.type());

    final Map<@NotNull String, @NotNull ? extends Val> tagValues = data._raw().tagValues();
    final Map<String, Val> replacements = new HashMap<>();

    for (final Map.Entry<String, ReqOutputTagProjectionEntry> entry : projection.tagProjections().entrySet()) {
      final String tagName = entry.getKey();
      final Tag tag = data.type().tagsMap().get(tagName);

      final ReqOutputTagProjectionEntry tagProjectionEntry = entry.getValue();
      final ReqOutputModelProjection<?, ?, ?> modelProjection = tagProjectionEntry.projection();
      final boolean required = modelProjection.required();

      Val val = tagValues.get(tagName);

      if (val != null) {
        final Datum datum = val.getDatum();

        if (datum != null) {
          final DatumPruningResult datumPruningResult = context.withStackItem(
              new DataTraversalContext.TagStackItem(tag),
              () -> pruneDatum(datum, modelProjection)
          );

          if (datumPruningResult instanceof Fail)
            return (Fail) datumPruningResult;

          if (datumPruningResult instanceof ReplaceDatum) {
            ReplaceDatum replaceDatum = (ReplaceDatum) datumPruningResult;
            replacements.put(tagName, replaceDatum.newDatum.asValue());
          } else {
            if (datumPruningResult instanceof UseError) {
              UseError useError = (UseError) datumPruningResult;
              replacements.put(tagName, tag.type.createValue(useError.error));
//            } else if (datumPruningResult instanceof UseNull) {
//              replacements.put(tagName, tag.type.createValue(null));
            } // else keep
          }
        }
      }

      if (replacements.containsKey(tagName))
        val = replacements.get(tagName);

      if (required) {
        final String name = data.type().kind() == TypeKind.UNION ? String.format("tag '%s'", tagName) : "data";
        if (val == null) {
          return new Fail(operationError(String.format("Required %s is missing", name)));
        } else {
          final ErrorValue error = val.getError();
          if (error == null) {
            if (val.getDatum() == null)
              return new RemoveData(error(String.format("Required %s is null", name)));
          } else
            return new RemoveData(error(String.format(
                "Required %s is a [%d] error: %s",
                name,
                error.statusCode(),
                error.message()
            )));
        }
      }

    }

    if (replacements.isEmpty())
      return Keep.INSTANCE;
    else {
      final Data.Builder builder = data.type().createDataBuilder();
      for (final Map.Entry<String, ? extends Val> entry : tagValues.entrySet()) {
        String tagName = entry.getKey();

        final Val val = replacements.containsKey(tagName) ? replacements.get(tagName) : entry.getValue();
        builder._raw().setValue(data.type().tagsMap().get(tagName), val);
      }
      return new ReplaceData(builder);
    }
  }

  private @NotNull DatumPruningResult pruneDatum(
      @NotNull Datum datum,
      @NotNull ReqOutputModelProjection<?, ?, ?> projection) {

    projection = projection.normalizedForType(datum.type());

    switch (datum.type().kind()) {
      case RECORD:
        return pruneRecordDatum((RecordDatum) datum, (ReqOutputRecordModelProjection) projection);
      case MAP:
        return pruneMapDatum((MapDatum) datum, (ReqOutputMapModelProjection) projection);
      case LIST:
        return pruneListDatum((ListDatum) datum, (ReqOutputListModelProjection) projection);
      default:
        return Keep.INSTANCE;
    }
  }

  private @NotNull DatumPruningResult pruneRecordDatum(
      @NotNull RecordDatum datum,
      @NotNull ReqOutputRecordModelProjection projection) {

    final Map<@NotNull String, @NotNull ? extends Data> fieldsData = datum._raw().fieldsData();
    final Map<String, Data> replacements = new HashMap<>();

    for (final Map.Entry<String, ReqOutputFieldProjectionEntry> entry : projection.fieldProjections().entrySet()) {
      final String fieldName = entry.getKey();
      final Field field = datum.type().fieldsMap().get(fieldName);

      final ReqOutputFieldProjectionEntry fieldProjectionEntry = entry.getValue();
      final ReqOutputFieldProjection fieldProjection = fieldProjectionEntry.fieldProjection();
      final boolean required = fieldProjection.required();
      final ReqOutputVarProjection dataProjection = fieldProjection.varProjection();

      Data data = fieldsData.get(fieldName);

      if (data != null) {
        final Data data0 = data;
        final DataPruningResult dataPruningResult = context.withStackItem(
            new DataTraversalContext.FieldStackItem(field),
            () -> pruneData(data0, dataProjection)
        );

        if (dataPruningResult instanceof Fail)
          return (Fail) dataPruningResult;

        if (dataPruningResult instanceof ReplaceData) {
          ReplaceData replaceData = (ReplaceData) dataPruningResult;
          replacements.put(fieldName, replaceData.newData);
        } else if (dataPruningResult instanceof RemoveData) {
          RemoveData removeData = (RemoveData) dataPruningResult;
          if (required)
            return new UseError(
                removeData.reason,
                new ErrorValue(HttpStatusCode.PRECONDITION_FAILED, removeData.reason.toString())
            );
//            return new UseNull(removeData.reason);
          else
            replacements.put(fieldName, null);
        } // else keep
      }

      if (replacements.containsKey(fieldName))
        data = replacements.get(fieldName);

      if (required) {
        DatumPruningResult fieldBasedResult = checkRequiredData(data, "field", "'" + fieldName + "'");
        if (fieldBasedResult != Keep.INSTANCE)
          return fieldBasedResult;
      }
    }

    if (replacements.isEmpty())
      return Keep.INSTANCE;
    else {
      final RecordDatum.Builder builder = datum.type().createBuilder();
      for (final Map.Entry<String, ? extends Data> entry : fieldsData.entrySet()) {
        String fieldName = entry.getKey();
        final Field field = datum.type().fieldsMap().get(fieldName);

        final Data data = replacements.containsKey(fieldName) ? replacements.get(fieldName) : entry.getValue();
        builder._raw().setData(field, data);
      }
      return new ReplaceDatum(builder);
    }
  }

  private @NotNull DatumPruningResult pruneMapDatum(
      @NotNull MapDatum datum,
      @NotNull ReqOutputMapModelProjection projection) {

    final ReqOutputVarProjection itemsProjection = projection.itemsProjection();
    final boolean keysRequired = projection.keysRequired();
    final Map<Datum.Imm, Data> replacements = new HashMap<>();

    for (final Map.Entry<Datum.Imm, ? extends Data> entry : datum._raw().elements().entrySet()) {
      final Datum.Imm key = entry.getKey();
      Data data = entry.getValue();

      final Data data0 = data;
      final DataPruningResult prunedData =
          context.withStackItem(
              new DataTraversalContext.MapKeyStackItem(key),
              () -> pruneData(data0, itemsProjection)
          );

      if (prunedData instanceof RemoveData) {
        RemoveData removeData = (RemoveData) prunedData;
        if (keysRequired)
//          return new UseNull(removeData.reason);
          return new UseError(
              removeData.reason,
              new ErrorValue(HttpStatusCode.PRECONDITION_FAILED, removeData.reason.toString())
          );
        else
          replacements.put(key, null);
      } else if (prunedData instanceof ReplaceData) {
        ReplaceData replaceData = (ReplaceData) prunedData;
        replacements.put(key, replaceData.newData);
      } else if (prunedData instanceof Fail)
        return (Fail) prunedData;
      // else keep

      if (replacements.containsKey(key))
        data = replacements.get(key);

      if (keysRequired) {
        DataTraversalContext.MapKeyStackItem keyItem = new DataTraversalContext.MapKeyStackItem(key);
        DatumPruningResult itemBasedResult = checkRequiredData(data, "key", keyItem);
        if (itemBasedResult != Keep.INSTANCE)
          return itemBasedResult;
      }
    }

    if (replacements.isEmpty())
      return Keep.INSTANCE;
    else {
      final MapDatum.Builder builder = datum.type().createBuilder();
      for (final Map.Entry<Datum.Imm, ? extends Data> entry : datum._raw().elements().entrySet()) {
        final Datum.Imm key = entry.getKey();
        Data data = replacements.containsKey(key) ? replacements.get(key) : entry.getValue();
        builder._raw().elements().put(key, data);
      }
      return new ReplaceDatum(builder);
    }

  }

  private @NotNull DatumPruningResult pruneListDatum(
      @NotNull ListDatum datum,
      @NotNull ReqOutputListModelProjection projection) {

    final ReqOutputVarProjection itemsProjection = projection.itemsProjection();
    final Map<Integer, Data> replacements = new HashMap<>();

    int index = 0;

    for (Data data : datum._raw().elements()) {

      final Data data0 = data;
      final DataPruningResult prunedData =
          context.withStackItem(
              new DataTraversalContext.ListIndexStackItem(index),
              () -> pruneData(data0, itemsProjection)
          );

      if (prunedData instanceof RemoveData) {
        replacements.put(index, null);
      } else if (prunedData instanceof ReplaceData) {
        ReplaceData replaceData = (ReplaceData) prunedData;
        replacements.put(index, replaceData.newData);
      } else if (prunedData instanceof Fail)
        return (Fail) prunedData;
      // else keep

      index++;
    }

    if (replacements.isEmpty())
      return Keep.INSTANCE;
    else {
      final ListDatum.Builder builder = datum.type().createBuilder();
      index = 0;
      for (Data data : datum._raw().elements()) {
        if (replacements.containsKey(index))
          data = replacements.get(index);
        builder._raw().elements().add(data);
        index++;
      }
      return new ReplaceDatum(builder);
    }

  }

  private DatumPruningResult checkRequiredData(
      @Nullable Data data,
      @NotNull String name,
      @NotNull Object id) {

    if (data == null) {
      return new Fail(operationError(String.format("Required %s %s is missing", name, id)));
    } else if (data.type().kind() != TypeKind.UNION) {
      final Val val = data._raw().tagValues().get(DatumType.MONO_TAG_NAME);

      if (val == null) {
        return new Fail(operationError(String.format("Required %s %s is missing", name, id)));
      } else {
        final ErrorValue error = val.getError();
        if (error == null) {
          if (val.getDatum() == null) {
            final Reason reason = error(String.format("Required %s %s is null", name, id));
            return new UseError(
                reason,
                new ErrorValue(HttpStatusCode.PRECONDITION_FAILED, reason.toString())
            );
          }
//            return new UseNull(error(String.format("Required %s %s is null", name, id)));
        } else {
          Reason reason = error(String.format("Required %s %s is an error: '%s'", name, id, error.message()));
          return new UseError(reason, new ErrorValue(error.statusCode(), reason.toString()));
        }
      }
    }

    return Keep.INSTANCE;
  }

  private @NotNull Reason operationError(String message) { return new Reason(true, message, location()); }

  private @NotNull Reason error(String message) { return new Reason(false, message, location()); }

  private @NotNull ArrayList<DataTraversalContext.StackItem> location() {
    final ArrayList<DataTraversalContext.StackItem> items = new ArrayList<>(context.stack());
    Collections.reverse(items);
    return items;
  }

  public interface DataPruningResult {}

  public static class ReplaceData implements DataPruningResult {
    public final @NotNull Data newData;

    ReplaceData(final @NotNull Data data) {newData = data;}
  }

  public static class RemoveData implements DataPruningResult { // remove field or map entry
    final @NotNull Reason reason;

    RemoveData(final @NotNull Reason reason) {this.reason = reason;}
  }

  public interface DatumPruningResult {}

  public static class Keep implements DatumPruningResult, DataPruningResult {
    public static final Keep INSTANCE = new Keep();
  }

  public static class ReplaceDatum implements DatumPruningResult {
    final @NotNull Datum newDatum;

    ReplaceDatum(final @NotNull Datum data) {newDatum = data;}
  }

  public static class UseError implements DatumPruningResult {
    final @NotNull Reason reason;
    final @NotNull ErrorValue error;

    UseError(
        final @NotNull Reason reason,
        final @NotNull ErrorValue error) {
      this.reason = reason;
      this.error = error;
    }
  }

//  public static class UseNull implements DatumPruningResult {
//    final @NotNull Reason reason;
//
//    UseNull(final @NotNull Reason reason) {this.reason = reason;}
//  }

  public static class Fail implements DatumPruningResult, DataPruningResult {
    public final @NotNull Reason reason;

    Fail(final @NotNull Reason reason) {this.reason = reason;}
  }

  public static class Reason {
    public final boolean isOperationError;
    public final @NotNull String message;
    public final @NotNull List<DataTraversalContext.StackItem> location;

    Reason(
        final boolean isOperationError,
        @NotNull String message,
        @NotNull List<DataTraversalContext.StackItem> location) {
      this.isOperationError = isOperationError;
      this.message = message;
      this.location = location;
    }

    @Override
    public @NotNull String toString() {
      String location = this.location.stream().map(DataTraversalContext.StackItem::toString).collect(Collectors.joining());
      return location.trim().isEmpty() ?
             message :
             location + " : " +
             message;
    }
  }
}
