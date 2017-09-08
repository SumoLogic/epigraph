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

package ws.epigraph.projections.op.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ModelNormalizationContext;
import ws.epigraph.projections.abs.AbstractVarProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.AbstractOpModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.*;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpOutputModelProjection<
    MP extends OpOutputModelProjection</*MP*/?, /*SMP*/?, /*M*/?, /*D*/?>,
    SMP extends OpOutputModelProjection</*MP*/?, SMP, ?, ?>,
    M extends DatumTypeApi,
    D extends GDatum
    > extends AbstractOpModelProjection<MP, SMP, M> {

  protected /*final*/ boolean flagged;
  protected /*final*/ @Nullable D defaultValue;

  protected OpOutputModelProjection(
      @NotNull M model,
      boolean flagged,
      @Nullable D defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable MP metaProjection,
      @Nullable List<SMP> tails,
      @NotNull TextLocation location
  ) {
    super(model, metaProjection, params, annotations, tails, location);
    this.flagged = flagged;
    this.defaultValue = defaultValue;
    // check that defaultValue is covered by the projection? (all required parts are present)
  }

  protected OpOutputModelProjection(final @NotNull M model, final @NotNull TextLocation location) {
    super(model, location);
  }

  public boolean flagged() { return flagged; }

  public @Nullable D defaultValue() { return defaultValue; }

  @Override
  public void setEntityProjection(final @NotNull AbstractVarProjection<?, ?, ?> entityProjection) {
    super.setEntityProjection(entityProjection);
    if (entityProjection instanceof OpOutputVarProjection) {
      OpOutputVarProjection o = (OpOutputVarProjection) entityProjection;
      if (o.flagged())
        flagged = true;
      else if (flagged())
        o.flagged = true;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  protected SMP merge(
      final @NotNull M model,
      final @NotNull List<SMP> modelProjections,
      final @NotNull OpParams mergedParams,
      final Annotations mergedAnnotations,
      final @Nullable MP mergedMetaProjection,
      final @Nullable List<SMP> mergedTails) {

    boolean mergedFlagged = modelProjections.stream().anyMatch(mp -> mp.flagged());
    D mergedDefault = modelProjections.stream()
        .map(m -> (D) m.defaultValue())
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null); // todo detect clashes and throw proper exception

    return merge(
        model,
        mergedFlagged,
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
      boolean mergedFlagged,
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
    this.flagged = value.flagged();
    this.defaultValue = (D) value.defaultValue();
    super.resolve(name, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected @NotNull ModelNormalizationContext<M, SMP> newNormalizationContext() {
    return new ModelNormalizationContext<>(m -> {
      switch (m.kind()) {
        case RECORD:
          return (SMP) new OpOutputRecordModelProjection((RecordTypeApi) m, TextLocation.UNKNOWN);
        case MAP:
          return (SMP) new OpOutputMapModelProjection((MapTypeApi) m, TextLocation.UNKNOWN);
        case LIST:
          return (SMP) new OpOutputListModelProjection((ListTypeApi) m, TextLocation.UNKNOWN);
        case PRIMITIVE:
          return (SMP) new OpOutputPrimitiveModelProjection((PrimitiveTypeApi) m, TextLocation.UNKNOWN);
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
    OpOutputModelProjection<?, ?, ?, ?> that = (OpOutputModelProjection<?, ?, ?, ?>) o;
    return flagged == that.flagged && Objects.equals(defaultValue, that.defaultValue);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), flagged, defaultValue); }
}
