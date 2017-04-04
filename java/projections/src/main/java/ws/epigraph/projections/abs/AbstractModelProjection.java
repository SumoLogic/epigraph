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

package ws.epigraph.projections.abs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.names.TypeName;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ModelNormalizationContext;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenProjectionReference;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.DatumTypeApi;

import java.util.*;
import java.util.stream.Collectors;

import static ws.epigraph.projections.ProjectionUtils.findUniqueName;
import static ws.epigraph.projections.ProjectionUtils.linearizeModelTails;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractModelProjection<
    MP extends GenModelProjection</*MP*/?, /*SMP*/?, /*SMP*/?, ?>,
    SMP extends GenModelProjection</*MP*/?, /*SMP*/?, SMP, ?>,
    M extends DatumTypeApi> implements GenModelProjection<MP, SMP, SMP, M> {

  protected final @NotNull M model;
  private /*final*/ @Nullable ProjectionReferenceName name;
  protected /*final*/ @Nullable MP metaProjection;
  protected /*final*/ @NotNull Annotations annotations;
  protected /*final*/ @Nullable List<SMP> polymorphicTails;

  private final @NotNull TextLocation location;

  protected boolean isReference;
  protected boolean isResolved;

  private final List<Runnable> onResolvedCallbacks = new ArrayList<>();

  private final Map<TypeName, SMP> normalizedCache = new HashMap<>();

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

    isReference = false;
    isResolved = true;

    validateTails();
  }

  protected AbstractModelProjection(@NotNull M model, @NotNull TextLocation location) {
    this.model = model;
    metaProjection = null;
    annotations = Annotations.EMPTY;
    this.location = location;

    isReference = true;
    isResolved = false;
  }

  @Override
  public @NotNull M type() { return model; }

  @Override
  public @Nullable MP metaProjection() { return metaProjection; }

  @Override
  public @NotNull Annotations annotations() { return annotations; }

  @Override
  public @Nullable List<SMP> polymorphicTails() { return polymorphicTails; }

  protected abstract @NotNull ModelNormalizationContext<M, SMP> newNormalizationContext();

  @SuppressWarnings("unchecked")
  @Override
  public SMP normalizedForType(final @NotNull DatumTypeApi targetType, boolean keepPhantomTails) {
    // keep in sync with AbstractVarProjection.normalizedForType
    assertResolved();
    if (targetType.equals(type()))
      return self();

    return ModelNormalizationContext.withContext(
        this::newNormalizationContext,
        context -> {
          SMP ref = normalizedCache.get(targetType.name());
          if (ref != null)
            return ref;

          final List<SMP> linearizedTails = linearizeModelTails(targetType, polymorphicTails());

          final DatumTypeApi effectiveType = ProjectionUtils.mostSpecific(
              targetType,
              linearizedTails.isEmpty() ? this.type() : linearizedTails.get(0).type(),
              this.type()
          );

          ProjectionReferenceName normalizedRefName = null;
          if (name == null) {
            ref = context.newReference((M) effectiveType);
            normalizedCache.put(targetType.name(), ref);
          } else {
            ref = context.visited().get(name);

            if (ref != null)
              return ref;

            ref = context.newReference((M) effectiveType);
            context.visited().put(name, ref);
            normalizedRefName = ProjectionUtils.normalizedTailNamespace(
                this.name,
                effectiveType,
                ProjectionUtils.sameNamespace(type().name(), effectiveType.name())
            );
          }

          final List<SMP> effectiveProjections = new ArrayList<>(linearizedTails);
          effectiveProjections.add(self()); //we're the least specific projection

          final List<SMP> mergedTails = mergeTails(effectiveProjections, keepPhantomTails);

          final List<SMP> filteredMergedTails;
          if (mergedTails == null)
            filteredMergedTails = null;
          else {
            filteredMergedTails = mergedTails
                .stream()
                // remove 'uninteresting' tails which already describe `effectiveType`
                .filter(t -> (!t.type().isAssignableFrom(effectiveType)) &&
                             (keepPhantomTails || effectiveType.isAssignableFrom(t.type())))
//            .map(t -> t.normalizedForType(targetType))
                .collect(Collectors.toList());
          }

          List<SMP> projectionsToMerge = effectiveProjections
              .stream()
              .filter(p -> p.type().isAssignableFrom(effectiveType))
              .collect(Collectors.toList());

          SMP res = merge((M) effectiveType, filteredMergedTails, projectionsToMerge, keepPhantomTails);
          assert res != null; // since effectiveProjections is non-empty, at least self is there
          res = postNormalizedForType(targetType, keepPhantomTails, res);

          ((GenProjectionReference<SMP>) ref).resolve(normalizedRefName, res);
          return ref;
        }
    );

  }

  /**
   * Called after {@code normalizeForType} is performed. Can perform any extra steps and return a modified version.
   */
  @NotNull
  protected SMP postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      boolean keepPhantomTails,
      @NotNull SMP normalizationResult) {
    return normalizationResult;
  }

  @SuppressWarnings("unchecked")
  @Override
  /* static */
  public SMP merge(
      final @NotNull M model,
      final @NotNull List<SMP> modelProjections,
      final boolean keepPhantomTails) {

    final List<SMP> mergedTails = mergeTails(modelProjections, keepPhantomTails);
    return merge(model, mergedTails, modelProjections, keepPhantomTails);
  }

  @SuppressWarnings("unchecked")
  /* static */
  private @Nullable SMP merge(
      final @NotNull M effectiveType,
      final @Nullable List<SMP> mergedTails,
      final @NotNull List<SMP> modelProjections,
      final boolean keepPhantomTails) {

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
          .merge((M) metaModel, metaProjectionsList, keepPhantomTails)
          .normalizedForType(metaModel, keepPhantomTails);
    }

    SMP res = merge(
        effectiveType,
        modelProjections,
        Annotations.merge(annotationsList),
        mergedMetaProjection,
        mergedTails,
        keepPhantomTails
    );
    final ProjectionReferenceName mergedRefName = findUniqueName(modelProjections);
    if (mergedRefName != null) res.setReferenceName(mergedRefName);
    return res;
  }

  @SuppressWarnings("unchecked")
  private @Nullable List<SMP> mergeTails(final @NotNull List<SMP> sources, boolean keepPhantomTails) {
    Map<DatumTypeApi, List<SMP>> tailsByType = null;

    for (final SMP projection : sources) {
      final List<SMP> tails = projection.polymorphicTails();
      if (tails != null) {
        if (tailsByType == null) tailsByType = new LinkedHashMap<>();
        for (SMP tail : tails) {
          List<SMP> collectedTails = tailsByType.computeIfAbsent(tail.type(), k -> new ArrayList<>());
          collectedTails.add(tail);
        }
      }
    }

    if (tailsByType == null) return null;

    List<SMP> mergedTails = new ArrayList<>(tailsByType.size());

    for (final Map.Entry<DatumTypeApi, List<SMP>> entry : tailsByType.entrySet()) {
      mergedTails.add(
          merge(
              (M) entry.getKey(),
              mergeTails(entry.getValue(), keepPhantomTails),
              entry.getValue(),
              keepPhantomTails
          )
      );
    }

    return mergedTails;
  }

  protected SMP merge(
      @NotNull M model,
      @NotNull List<SMP> modelProjections,
      @NotNull Annotations mergedAnnotations,
      @Nullable MP mergedMetaProjection,
      @Nullable List<SMP> mergedTails,
      final boolean keepPhantomTails) {

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
        if (tail.type().isAssignableFrom(type())) {
          throw new IllegalArgumentException(
              String.format(
                  "Tail type '%s' is assignable from model type '%s'. Tail defined at %s",
                  tail.type().name(),
                  type().name(),
                  tail.location()
              )
          );
        }
      }
    }
  }

  @Override
  public @Nullable ProjectionReferenceName referenceName() { return name; }

  @Override
  public void setReferenceName(final @NotNull ProjectionReferenceName referenceName) {
    if (name != null) throw new
        IllegalArgumentException(
        String.format("Can't override reference name (%s => %s)", name, referenceName)
    );
    this.name = referenceName;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void resolve(final ProjectionReferenceName name, final @NotNull SMP value) {
    if (!isReference)
      throw new IllegalStateException("Non-reference projection can't be resolved");
    if (isResolved())
      throw new IllegalStateException("Attempt to resolve already resolved reference: " + this.name);
    if (!type().isAssignableFrom(value.type()))
      throw new IllegalStateException(String.format(
          "Value type '%s' is incompatible with reference type '%s'",
          value.type().name(),
          this.type().name()
      ));

    this.isResolved = true;
    this.name = name;
    this.metaProjection = (MP) value.metaProjection();
    this.annotations = value.annotations();
    this.polymorphicTails = value.polymorphicTails();

    for (final Runnable callback : onResolvedCallbacks) callback.run();
    onResolvedCallbacks.clear();
  }

  @Override
  public boolean isResolved() { return isResolved; }

  @Override
  public void runOnResolved(final @NotNull Runnable callback) {
    if (isResolved())
      callback.run();
    else
      onResolvedCallbacks.add(callback);
  }

  protected void assertResolved() {
    if (!isResolved())
      throw new IllegalStateException("Projection is not resolved: " + name);
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
