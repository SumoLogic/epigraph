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

package ws.epigraph.gdata.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.*;
import ws.epigraph.projections.gen.*;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.FieldApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.EntityTypeApi;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenGDataValidator<
    VP extends GenEntityProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection<TP, /*MP*/?, /*SMP*/?, /*TMP*/?, /*M*/?>,
    RMP extends GenRecordModelProjection<VP, TP, MP, RMP, FPE, FP, ?>,
    MMP extends GenMapModelProjection<VP, TP, MP, MMP, ?>,
    LMP extends GenListModelProjection<VP, TP, MP, LMP, ?>,
    PMP extends GenPrimitiveModelProjection<TP, MP, PMP, ?>,
    FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
    FP extends GenFieldProjection<VP, TP, MP, FP>
    > {

  protected final @NotNull TypesResolver typesResolver;
  protected final @NotNull GDataValidationContext context = new GDataValidationContext();
  protected final @NotNull Map<GData, Set<VP>> visited = new IdentityHashMap<>();

  protected GenGDataValidator(final @NotNull TypesResolver resolver) {typesResolver = resolver;}

  public @NotNull List<? extends GDataValidationError> errors() { return context.errors(); }

  public void validateData(@Nullable GData data, @NotNull VP projection) {
    if (data == null) return;

    final VP normalizedProjection;
    final TypeRef typeRef = data.typeRef();
    if (typeRef == null) {
      normalizedProjection = projection;
    } else {
      final EntityTypeApi type = typeRef.resolveEntityType(typesResolver);
      if (type == null) {
        context.addError("Can't resolve var type '" + typeRef.toString() + "'", data.location());
        return;
      }
      normalizedProjection = projection.normalizedForType(type);
    }

    Set<VP> checkedProjections = visited.get(data);
    if (checkedProjections == null) {
      checkedProjections = Collections.newSetFromMap(new IdentityHashMap<VP, Boolean>());
    } else if (checkedProjections.contains(normalizedProjection))
      return;

    checkedProjections.add(normalizedProjection);

    validateDataOnly(data, normalizedProjection);

    for (final TP tagProjection : normalizedProjection.tagProjections().values()) {
      final TagApi tag = tagProjection.tag();
      final String tagName = tag.name();
      context.withStackItem(new GDataValidationContext.TagStackItem(tagName), () -> {
        final GDatum datum = data.tags().get(tagName);
        if (datum != null)
          validateDatum(datum, tagProjection.projection());
      });
    }

    checkedProjections.remove(normalizedProjection);
    if (checkedProjections.isEmpty())
      visited.remove(data);
  }

  protected void validateDataOnly(@NotNull GData data, @NotNull VP projection) {}

  @SuppressWarnings("unchecked")
  public void validateDatum(@NotNull GDatum datum, @NotNull MP projection) {

    final MP normalizedProjection;
    final TypeRef typeRef = datum.typeRef();
    if (typeRef == null) {
      normalizedProjection = projection;
    } else {
      final DatumTypeApi type = typeRef.resolveDatumType(typesResolver);
      if (type == null) {
        context.addError("Can't resolve model type '" + typeRef.toString() + "'", datum.location());
        return;
      }
      normalizedProjection = (MP) projection.normalizedForType(type);
    }

    validateDatumOnly(datum, normalizedProjection);

    if (!(datum instanceof GNullDatum))
      switch (normalizedProjection.type().kind()) {
        case RECORD:
          validateRecordDatum((GRecordDatum) datum, (RMP) normalizedProjection);
          break;
        case MAP:
          validateMapDatum((GMapDatum) datum, (MMP) normalizedProjection);
          break;
        case LIST:
          validateListDatum((GListDatum) datum, (LMP) normalizedProjection);
          break;
        case PRIMITIVE:
          validatePrimitiveDatum((GPrimitiveDatum) datum, (PMP) normalizedProjection);
          break;
        default:
          throw new RuntimeException(
              "Unsupported model kind: " + normalizedProjection.type().kind().getClass().getName());
      }
  }

  protected void validateDatumOnly(@NotNull GDatum datum, @NotNull MP projection) {}

  protected void validateDataValue(@NotNull GDataValue gDataValue, @NotNull VP projection) {
    if (gDataValue instanceof GData) {
      GData gData = (GData) gDataValue;
      validateData(gData, projection);
    } else if (gDataValue instanceof GDatum) {
      GDatum gDatum = (GDatum) gDataValue;
      final TP tagProjection = projection.singleTagProjection();
      if (tagProjection != null) {
        validateDatum(gDatum, tagProjection.projection());
      }
    } else throw new RuntimeException("Unknown GDataValue type: " + gDataValue.getClass().getName());
  }

  public void validateRecordDatum(@NotNull GRecordDatum datum, @NotNull RMP projection) {
    validateRecordDatumOnly(datum, projection);

    for (final FPE fpe : projection.fieldProjections().values()) {
      final FieldApi field = fpe.field();
      final String fieldName = field.name();

      context.withStackItem(new GDataValidationContext.FieldStackItem(fieldName), () -> {
        final GDataValue dataValue = datum.fields().get(fieldName);
        if (dataValue != null) {
          validateDataValue(dataValue, fpe.fieldProjection().projection());
        }
      });
    }
  }

  protected void validateRecordDatumOnly(@NotNull GRecordDatum datum, @NotNull RMP projection) { }

  public void validateMapDatum(@NotNull GMapDatum datum, @NotNull MMP projection) {
    validateMapDatumOnly(datum, projection);

    for (final Map.Entry<GDatum, GDataValue> entry : datum.entries().entrySet()) {
      final GDatum key = entry.getKey();

      context.withStackItem(
          new GDataValidationContext.MapKeyStackItem(key),
          () -> validateDataValue(entry.getValue(), projection.itemsProjection())
      );
    }
  }

  protected void validateMapDatumOnly(@NotNull GMapDatum datum, @NotNull MMP projection) {}

  public void validateListDatum(@NotNull GListDatum datum, @NotNull LMP projection) {
    validateListDatumOnly(datum, projection);

    int index = 0;
    for (final GDataValue data : datum.values()) {
      context.withStackItem(
          new GDataValidationContext.ListIndexStackItem(index++),
          () -> validateDataValue(data, projection.itemsProjection())
      );
    }
  }

  protected void validateListDatumOnly(@NotNull GListDatum datum, @NotNull LMP projection) { }

  public void validatePrimitiveDatum(@NotNull GPrimitiveDatum datum, @NotNull PMP projection) { }

}
