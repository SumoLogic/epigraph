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

package ws.epigraph.projections.abs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.types.DatumTypeApi;

import java.util.*;
import java.util.stream.Collectors;

import static ws.epigraph.projections.ProjectionUtils.linearizeModelTails;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractModelProjection<
    MP extends GenModelProjection</*MP*/?, /*SMP*/?, /*SMP*/?, ?>,
    SMP extends GenModelProjection</*MP*/?, /*SMP*/?, SMP, ?>,
    M extends DatumTypeApi> implements GenModelProjection<MP, SMP, SMP, M> {

  protected final @NotNull M model;
  protected final @Nullable MP metaProjection;
  protected final @NotNull Annotations annotations;
  protected final @Nullable List<SMP> polymorphicTails;

  private final @NotNull TextLocation location;

  protected AbstractModelProjection(
      @NotNull M model,
      @Nullable MP metaProjection,
      @NotNull Annotations annotations,
      @Nullable List<SMP> polymorphicTails,
      @NotNull TextLocation location
  ) {
    this.model = model;
    this.metaProjection = metaProjection;
    this.annotations = annotations;
    this.polymorphicTails = polymorphicTails;
    this.location = location;

    // we're allowed to have unrelated tails now
//    validateTails();
  }

  @Override
  public @NotNull M model() { return model; }

  @Override
  public @Nullable MP metaProjection() { return metaProjection; }

  @Override
  public @NotNull Annotations annotations() { return annotations; }

  @Override
  public @Nullable List<SMP> polymorphicTails() { return polymorphicTails; }

  @SuppressWarnings("unchecked")
  @Override
  public @NotNull SMP normalizedForType(final @NotNull DatumTypeApi targetType) {

    final List<SMP> linearizedTails = linearizeModelTails(targetType, polymorphicTails());

    final DatumTypeApi effectiveType = ProjectionUtils.mostSpecific(
        targetType,
        linearizedTails.isEmpty() ? this.model() : linearizedTails.get(0).model(),
        this.model()
    );

    final List<SMP> effectiveProjections = new ArrayList<>(linearizedTails);
    effectiveProjections.add(self()); //we're the least specific projection

    final List<SMP> mergedTails = mergeTails(effectiveProjections);
    final List<SMP> mergedNormalizedTails = mergedTails == null ? null : mergedTails
        .stream()
        .filter(t -> !t.model().isAssignableFrom(effectiveType)) // remove 'uninteresting' tails that aren't specific enough
        .map(t -> t.normalizedForType(targetType))
        .collect(Collectors.toList());


    final SMP mergeResult = merge((M) effectiveType, mergedNormalizedTails, effectiveProjections);
    assert mergeResult != null; // since effectiveProjections is non-empty, at least self is there
    return mergeResult;
  }

  @SuppressWarnings("unchecked")
  @Override
  /* static */
  public SMP merge(
      final @NotNull M model,
      final @NotNull List<SMP> modelProjections) {

    final List<SMP> mergedTails = mergeTails(modelProjections);
    return merge(model, mergedTails, modelProjections);
  }

  @SuppressWarnings("unchecked")
  /* static */
  private @Nullable SMP merge(
      final @NotNull M effectiveType,
      final @Nullable List<SMP> mergedTails,
      final @NotNull List<SMP> modelProjections) {

    if (modelProjections.isEmpty()) return null;

    List<Annotations> annotationsList = new ArrayList<>();
    List<MP> metaProjectionsList = new ArrayList<>();

    for (final GenModelProjection<?, ?, ?, ?> p : modelProjections) {
      AbstractModelProjection<MP, SMP, ?> mp = (AbstractModelProjection<MP, SMP, ?>) p;
      annotationsList.add(mp.annotations());
      final @Nullable MP meta = mp.metaProjection();
      if (meta != null) metaProjectionsList.add(meta);
    }

    final MP mergedMetaProjection;
    if (metaProjectionsList.isEmpty()) mergedMetaProjection = null;
    else {
      final MP metaProjection = metaProjectionsList.get(0);
      DatumTypeApi metaModel = effectiveType.metaType();
      assert metaModel != null; // since we have a projection for it
      mergedMetaProjection = (MP) ((GenModelProjection<MP, MP, MP, M>) metaProjection)
          .merge((M) metaModel, metaProjectionsList)
          .normalizedForType(metaModel);
    }

    return merge(
        effectiveType,
        modelProjections,
        Annotations.merge(annotationsList),
        mergedMetaProjection,
        mergedTails
    );
  }

  @SuppressWarnings("unchecked")
  private @Nullable List<SMP> mergeTails(final @NotNull List<SMP> sources) {
    Map<DatumTypeApi, List<SMP>> tailsByType = null;

    for (final SMP projection : sources) {
      final List<SMP> tails = projection.polymorphicTails();
      if (tails != null) {
        if (tailsByType == null) tailsByType = new HashMap<>();
        for (SMP tail : tails) {
          List<SMP> collectedTails = tailsByType.computeIfAbsent(tail.model(), k -> new ArrayList<>());
          collectedTails.add(tail);
        }
      }
    }

    if (tailsByType == null) return null;

    List<SMP> mergedTails = new ArrayList<>(tailsByType.size());

    for (final Map.Entry<DatumTypeApi, List<SMP>> entry : tailsByType.entrySet()) {
      mergedTails.add(merge((M) entry.getKey(), mergeTails(entry.getValue()), entry.getValue()));
    }

    return mergedTails;
  }

  protected SMP merge(
      @NotNull M model,
      @NotNull List<SMP> modelProjections,
      @NotNull Annotations mergedAnnotations,
      @Nullable MP mergedMetaProjection,
      @Nullable List<SMP> mergedTails) {

    throw new RuntimeException("unimplemented"); // todo
  }

  @SuppressWarnings("unchecked")
  private @NotNull SMP self() { return (SMP) this; }

  @Override
  public @NotNull TextLocation location() { return location; }

  private void validateTails() {
    final @Nullable List<SMP> tails = polymorphicTails();
    if (tails != null) {
      for (final SMP tail : tails) {
        if (!model().isAssignableFrom(tail.model())) { // warn about useless cases when tail.type == type?
          throw new IllegalArgumentException(
              String.format(
                  "Tail type '%s' is not a sub-type of var type '%s'. Tail defined at %s",
                  tail.model().name(),
                  model().name(),
                  tail.location()
              )
          );
        }
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractModelProjection<?, ?, ?> that = (AbstractModelProjection<?, ?, ?>) o;
    return Objects.equals(model, that.model) &&
           Objects.equals(metaProjection, that.metaProjection) &&
           Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(model, metaProjection, annotations);
  }
}
