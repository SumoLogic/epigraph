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

package ws.epigraph.validation.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.*;
import ws.epigraph.projections.gen.*;
import ws.epigraph.types.FieldApi;
import ws.epigraph.types.RecordType;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.Type;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenDataValidator<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, /*SMP*/?, /*TMP*/?, /*M*/?>,
    RMP extends GenRecordModelProjection<VP, TP, MP, RMP, FPE, FP, ?>,
    MMP extends GenMapModelProjection<VP, TP, MP, MMP, ?>,
    LMP extends GenListModelProjection<VP, TP, MP, LMP, ?>,
    PMP extends GenPrimitiveModelProjection<MP, PMP, ?>,
    FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
    FP extends GenFieldProjection<VP, TP, MP, FP>
    > {

  protected final @NotNull DataValidationContext context = new DataValidationContext();
  protected final @NotNull Map<Data, Set<VP>> visited = new IdentityHashMap<>();

  public @NotNull List<? extends DataValidationError> errors() { return context.errors(); }

  public void validateData(@Nullable Data data, @NotNull VP projection) {
    if (data == null) return;

    Set<VP> checkedProjections = visited.get(data);
    if (checkedProjections == null) {
      checkedProjections = Collections.newSetFromMap(new IdentityHashMap<VP, Boolean>());
    } else if (checkedProjections.contains(projection))
      return;

    checkedProjections.add(projection);

    validateDataOnly(data, projection);

    for (final TP tagProjection : projection.tagProjections().values()) {
      final TagApi tag = tagProjection.tag();
      context.withStackItem(new DataValidationContext.TagStackItem(tag), () -> {
        final Datum datum = data._raw().getDatum((Type.Tag) tag);
        if (datum != null)
          validateDatum(datum, tagProjection.projection());
      });
    }

    checkedProjections.remove(projection);
    if (checkedProjections.isEmpty())
      visited.remove(data);
  }

  protected void validateDataOnly(@NotNull Data data, @NotNull VP projection) {}

  @SuppressWarnings("unchecked")
  public void validateDatum(@NotNull Datum datum, @NotNull MP projection) {
    validateDatumOnly(datum, projection);

    switch (projection.type().kind()) {
      case RECORD:
        validateRecordDatum((RecordDatum) datum, (RMP) projection);
        break;
      case MAP:
        validateMapDatum((MapDatum) datum, (MMP) projection);
        break;
      case LIST:
        validateListDatum((ListDatum) datum, (LMP) projection);
        break;
      case PRIMITIVE:
        validatePrimitiveDatum((PrimitiveDatum<?>) datum, (PMP) projection);
        break;
      default:
        throw new RuntimeException("Unsupported model kind: " + projection.type().kind().getClass().getName());
    }
  }

  protected void validateDatumOnly(@NotNull Datum datum, @NotNull MP projection) {}

  public void validateRecordDatum(@NotNull RecordDatum datum, @NotNull RMP projection) {
    validateRecordDatumOnly(datum, projection);

    for (final FPE fpe : projection.fieldProjections().values()) {
      final FieldApi field = fpe.field();

      context.withStackItem(new DataValidationContext.FieldStackItem(field), () -> {
        final Data data = datum._raw().getData((RecordType.Field) field);
        if (data != null)
          validateData(data, fpe.fieldProjection().varProjection());
      });
    }
  }

  protected void validateRecordDatumOnly(@NotNull RecordDatum datum, @NotNull RMP projection) { }

  public void validateMapDatum(@NotNull MapDatum datum, @NotNull MMP projection) {
    validateMapDatumOnly(datum, projection);

    for (final Map.Entry<Datum.Imm, ? extends Data> entry : datum._raw().elements().entrySet()) {
      final Datum.Imm key = entry.getKey();

      context.withStackItem(new DataValidationContext.MapKeyStackItem(key), () -> {
        validateData(entry.getValue(), projection.itemsProjection());
      });
    }
  }

  protected void validateMapDatumOnly(@NotNull MapDatum datum, @NotNull MMP projection) {}

  public void validateListDatum(@NotNull ListDatum datum, @NotNull LMP projection) {
    validateListDatumOnly(datum, projection);

    int index = 0;
    for (final Data data : datum._raw().elements()) {
      context.withStackItem(new DataValidationContext.ListIndexStackItem(index++), () -> {
        validateData(data, projection.itemsProjection());
      });
    }
  }

  protected void validateListDatumOnly(@NotNull ListDatum datum, @NotNull LMP projection) { }

  public void validatePrimitiveDatum(@NotNull PrimitiveDatum<?> datum, @NotNull PMP projection) { }

}
