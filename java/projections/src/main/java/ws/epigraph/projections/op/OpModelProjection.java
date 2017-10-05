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

package ws.epigraph.projections.op;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ModelNormalizationContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.*;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpModelProjection<
    MP extends OpModelProjection</*MP*/?, /*SMP*/?, /*M*/?, /*D*/?>,
    SMP extends OpModelProjection</*MP*/?, SMP, ?, ?>,
    M extends DatumTypeApi,
    D extends GDatum
    > extends AbstractOpModelProjection<MP, SMP, M> {

  protected /*final*/ @Nullable D defaultValue;

  protected OpModelProjection(
      @NotNull M model,
      boolean flag,
      @Nullable D defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable MP metaProjection,
      @Nullable List<SMP> tails,
      @NotNull TextLocation location
  ) {
    super(model, flag, metaProjection, params, annotations, tails, location);
    this.defaultValue = defaultValue;
    // check that defaultValue is covered by the projection? (all required parts are present)
  }

  protected OpModelProjection(final @NotNull M model, final @NotNull TextLocation location) {
    super(model, location);
  }

  public @Nullable D defaultValue() { return defaultValue; }

  @SuppressWarnings("unchecked")
  @Override
  protected SMP merge(
      final @NotNull M model,
      final boolean mergedFlag,
      final @NotNull List<SMP> modelProjections,
      final @NotNull OpParams mergedParams,
      final Annotations mergedAnnotations,
      final @Nullable MP mergedMetaProjection,
      final @Nullable List<SMP> mergedTails) {

    D mergedDefault = modelProjections.stream()
        .map(m -> (D) m.defaultValue())
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null); // todo detect clashes and throw proper exception

    return merge(
        model,
        mergedFlag,
        mergedDefault,
        modelProjections,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedTails
    );
  }

  protected abstract SMP merge(
      @NotNull M model,
      boolean mergedFlag,
      @Nullable D mergedDefault,
      @NotNull List<SMP> modelProjections,
      @NotNull OpParams mergedParams,
      @NotNull Annotations mergedAnnotations,
      @Nullable MP mergedMetaProjection,
      @Nullable List<SMP> mergedTails
  );

  @SuppressWarnings("unchecked")
  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull SMP value) {
    preResolveCheck(value);
    this.defaultValue = (D) value.defaultValue();
    super.resolve(name, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected @NotNull ModelNormalizationContext<M, SMP> newNormalizationContext() {
    return new ModelNormalizationContext<>(m -> {
      switch (m.kind()) {
        case RECORD:
          return (SMP) new OpRecordModelProjection((RecordTypeApi) m, TextLocation.UNKNOWN);
        case MAP:
          return (SMP) new OpMapModelProjection((MapTypeApi) m, TextLocation.UNKNOWN);
        case LIST:
          return (SMP) new OpListModelProjection((ListTypeApi) m, TextLocation.UNKNOWN);
        case PRIMITIVE:
          return (SMP) new OpPrimitiveModelProjection((PrimitiveTypeApi) m, TextLocation.UNKNOWN);
        default:
          throw new IllegalArgumentException("Unsupported model kind: " + m.kind());
      }
    });
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpModelProjection<?, ?, ?, ?> that = (OpModelProjection<?, ?, ?, ?>) o;
    return Objects.equals(defaultValue, that.defaultValue);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), defaultValue); }
}
