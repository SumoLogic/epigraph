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
import ws.epigraph.projections.ModelNormalizationContext;
import ws.epigraph.projections.NormalizationContext;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.UnresolvedReferenceException;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.DatumTypeApi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static ws.epigraph.projections.ProjectionUtils.buildReferenceName;
import static ws.epigraph.projections.ProjectionUtils.linearizeModelTails;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractModelProjection<
    MP extends GenModelProjection</*MP*/?, /*SMP*/?, /*SMP*/?, ?>,
    SMP extends AbstractModelProjection</*MP*/?, SMP, ?>,
    M extends DatumTypeApi> implements GenModelProjection<MP, SMP, SMP, M> {

  protected final @NotNull M model;
  private /*final*/ @Nullable ProjectionReferenceName name;
  protected /*final*/ @Nullable MP metaProjection;
  protected /*final*/ @Nullable List<SMP> polymorphicTails;

  private /*final*/ @NotNull TextLocation location;

  protected boolean isReference;
  protected boolean isResolved;

  private final List<Runnable> onResolvedCallbacks = new ArrayList<>();

  private final Map<TypeName, NormalizedCacheItem> normalizedCache = new ConcurrentHashMap<>();

  protected @Nullable SMP normalizedFrom = null; // this = normalizedFrom ~ someType ?
  protected @Nullable AbstractVarProjection<?, ?, ?> entityProjection = null; // reference to self-entity, if any

  private final Throwable allocationTrace = new Throwable();

  protected AbstractModelProjection(
      @NotNull M model,
      @Nullable MP metaProjection,
      @Nullable List<SMP> polymorphicTails,
      @NotNull TextLocation location
  ) {
    assert polymorphicTails == null || !polymorphicTails.isEmpty();

    this.model = model;
    this.metaProjection = metaProjection;
    this.polymorphicTails = polymorphicTails;
    this.location = location;

    isReference = false;
    isResolved = true;

    validateTails();
  }

  protected AbstractModelProjection(@NotNull M model, @NotNull TextLocation location) {
    this.model = model;
    metaProjection = null;
    this.location = location;

    isReference = true;
    isResolved = false;
  }

  @Override
  public @NotNull M type() { return model; }

  @Override
  public @Nullable MP metaProjection() { return metaProjection; }

  @Override
  public @Nullable List<SMP> polymorphicTails() { return polymorphicTails; }

  protected abstract @NotNull ModelNormalizationContext<M, SMP> newNormalizationContext();

//  /**
//   * Updates cached version of normalized projections.
//   * <p>
//   * Used by schema parsers to assign normalized projection aliases, for example
//   * <code><pre>
//   *   outputProjection personProjection: Person = :id :~User $userProjection = :record (firstName)
//   * </pre></code>
//   * This will mark {@code personProjection} normalized to {@code User} type as {@code userProjection}
//   *
//   * @param targetType target type
//   * @param normalized normalized projection
//   */
//  @SuppressWarnings("unchecked")
//  public void setNormalizedForType(@NotNull DatumTypeApi targetType, @NotNull SMP normalized) {
//    TypeName typeName = targetType.name();
//    normalizedCache.put(typeName, new NormalizedCacheItem(normalized));
//    normalized.normalizedFrom = self();
//  }


  @SuppressWarnings("unchecked")
  public <EP extends AbstractVarProjection<EP, ?, ?>> void setNormalizedFrom(final @Nullable SMP normalizedFrom) {
    this.normalizedFrom = normalizedFrom;

    if (entityProjection != null && normalizedFrom != null) {

      EP ep = (EP) normalizedFrom.entityProjection;
      if (ep != null)
        ((EP) entityProjection).normalizedFrom = ep;
    }

  }

  public void setEntityProjection(@NotNull AbstractVarProjection<?, ?, ?> entityProjection) {
    this.entityProjection = entityProjection;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull SMP normalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @Nullable ProjectionReferenceName resultReferenceName) {

    // keep in sync with AbstractVarProjection.normalizedForType
    if (targetType.equals(type()))
      return self();

    assertResolved();
    assert (type().kind() == targetType.kind());

    return ModelNormalizationContext.withContext(
        this::newNormalizationContext,
        context -> {
          TypeName targetTypeName = targetType.name();
          NormalizedCacheItem cacheItem = normalizedCache.get(targetTypeName);
          // this projection is already being normalized. If we're in the same thread then it's OK to
          // return same (uninitialized) instance, it will get resolved higher in the stack
          // If this is another thread then we have to await for normalization to finish
          if (cacheItem != null)
            return cacheItem.allocatedByCurrentThread() ? cacheItem.mp : cacheItem.awaitForResolved();

          final List<SMP> linearizedTails = linearizeModelTails(targetType, polymorphicTails());

          final DatumTypeApi effectiveType = ProjectionUtils.mostSpecific(
              targetType,
              linearizedTails.isEmpty() ? this.type() : linearizedTails.get(0).type(),
              this.type()
          );

          if (effectiveType.equals(this.type()))
            return self();

          try {
            SMP ref;
            ProjectionReferenceName normalizedRefName = resultReferenceName;
            if (this.name == null) {
              ref = context.newReference((M) effectiveType, self());
              ref.setReferenceName(normalizedRefName);
              normalizedCache.put(targetTypeName, new NormalizedCacheItem(ref));
            } else {
              NormalizationContext.VisitedKey visitedKey =
                  new NormalizationContext.VisitedKey(this.name, effectiveType.name());
              ref = context.visited().get(visitedKey);

              if (ref != null)
                return ref;

              ref = context.newReference((M) effectiveType, self());
              context.visited().put(visitedKey, ref);

              if (normalizedRefName == null)
                normalizedRefName = ProjectionUtils.normalizedTailNamespace(
                    this.name,
                    effectiveType,
                    ProjectionUtils.sameNamespace(type().name(), effectiveType.name())
                );
            }

            final List<SMP> effectiveProjections = new ArrayList<>(linearizedTails);
            effectiveProjections.add(self()); //we're the least specific projection

            final List<SMP> mergedTails = mergeTails(effectiveProjections);

            final List<SMP> filteredMergedTails;
            if (mergedTails == null)
              filteredMergedTails = null;
            else {
              final List<SMP> tmp = mergedTails
                  .stream()
                  // remove 'uninteresting' tails which already describe `effectiveType`
                  .filter(t -> (!t.type().isAssignableFrom(effectiveType)) && effectiveType.isAssignableFrom(t.type()))
                  //            .map(t -> t.normalizedForType(targetType))
                  .collect(Collectors.toList());
              filteredMergedTails = tmp.isEmpty() ? null : tmp;
            }

            List<SMP> projectionsToMerge = effectiveProjections
                .stream()
                .filter(p -> p.type().isAssignableFrom(effectiveType))
                .collect(Collectors.toList());

            SMP res = merge((M) effectiveType, filteredMergedTails, projectionsToMerge);
            assert res != null; // since effectiveProjections is non-empty, at least self is there
            res = postNormalizedForType(targetType, res);
            res.normalizedFrom = self();

            ref.resolve(normalizedRefName, res);
            return ref;
          } catch (RuntimeException e) {
            normalizedCache.remove(targetTypeName);
            throw e;
          }
        }
    );

  }

  private final class NormalizedCacheItem {
    final long creatorThreadId;
    final @NotNull SMP mp;

    NormalizedCacheItem(final @NotNull SMP mp) {
      creatorThreadId = Thread.currentThread().getId();
      this.mp = mp;
    }

    boolean allocatedByCurrentThread() { return Thread.currentThread().getId() == creatorThreadId; }

    @NotNull SMP awaitForResolved() {
      assert !allocatedByCurrentThread();
      final CountDownLatch latch = new CountDownLatch(1);
      mp.runOnResolved(latch::countDown);
      try {
        latch.await();
      } catch (InterruptedException ignored) {
        Thread.currentThread().interrupt();
      }
      return mp;
    }
  }

  /**
   * Called after {@code normalizeForType} is performed. Can perform any extra steps and return a modified version.
   */
  protected @NotNull SMP postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      @NotNull SMP normalizationResult) { return normalizationResult; }

  @Override
  @SuppressWarnings("unchecked") /* static */ public @Nullable SMP merge(
      @NotNull M model,
      @NotNull List<SMP> modelProjections) {

    assert !modelProjections.isEmpty();

    modelProjections = new ArrayList<>(new LinkedHashSet<>(modelProjections));

    if (modelProjections.size() == 1) {
      return modelProjections.get(0);
    } else {
      Optional<SMP> unresolvedOpt = modelProjections.stream().filter(p -> !p.isResolved()).findFirst();
      if (unresolvedOpt.isPresent()) {
        String message = ModelNormalizationContext.withContext(
            this::newNormalizationContext,
            context -> {
              SMP origin = context.origin(unresolvedOpt.get());

              TextLocation loc = context.visited().entrySet().isEmpty() ? TextLocation.UNKNOWN :
                                 context.visited().entrySet().iterator().next().getValue().location();

              return String.format(
                  "Can't merge recursive projection '%s' with other projection at %s",
                  origin == null ? "<unknown>" : origin.referenceName(), loc
              );
            }
        );

        throw new IllegalArgumentException(message);
      } else
        return merge(model, mergeTails(modelProjections), modelProjections);
    }
  }

  @SuppressWarnings("unchecked")
  /* static */
  private @Nullable SMP merge(
      final @NotNull M effectiveType,
      final @Nullable List<SMP> mergedTails,
      final @NotNull List<SMP> modelProjections) {

    if (modelProjections.isEmpty()) return null;

    List<MP> metaProjectionsList = new ArrayList<>();

    for (final GenModelProjection<?, ?, ?, ?> p : modelProjections) {
      AbstractModelProjection<MP, SMP, ?> mp = (AbstractModelProjection<MP, SMP, ?>) p;
      final @Nullable MP meta = mp.metaProjection();
      if (meta != null) metaProjectionsList.add(meta);
    }

    final MP mergedMetaProjection;
    if (metaProjectionsList.isEmpty()) mergedMetaProjection = null;
    else {
      final MP metaProjection = metaProjectionsList.get(0);
      DatumTypeApi metaModel = effectiveType.metaType();
      assert metaModel != null; // since we have a projection for it
      //noinspection ConstantConditions
      mergedMetaProjection = (MP) ((GenModelProjection<MP, MP, MP, M>) metaProjection)
          .merge((M) metaModel, metaProjectionsList)
          .normalizedForType(metaModel);
    }

    final ProjectionReferenceName mergedRefName =
        buildReferenceName(modelProjections, modelProjections.get(0).location());
    SMP res = merge(
        effectiveType,
        modelProjections,
        mergedMetaProjection,
        mergedTails
    );
    if (mergedRefName != null) res.setReferenceName(mergedRefName);
    return res;
  }

  @SuppressWarnings("unchecked")
  private @Nullable List<SMP> mergeTails(final @NotNull List<SMP> sources) {
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
              mergeTails(entry.getValue()),
              entry.getValue()
          )
      );
    }

    return mergedTails;
  }

  protected abstract SMP merge(
      @NotNull M model,
      @NotNull List<SMP> modelProjections,
      @Nullable MP mergedMetaProjection,
      @Nullable List<SMP> mergedTails);

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
  public void setReferenceName(final @Nullable ProjectionReferenceName referenceName) {
    setReferenceName0(referenceName);
    if (entityProjection != null) entityProjection.setReferenceName0(referenceName);
  }

  public void setReferenceName0(final @Nullable ProjectionReferenceName referenceName) {
//    if (name != null) throw new IllegalArgumentException(
//        String.format("Can't override reference name (%s => %s)", name, referenceName)
//    );
    if (this.name == null)
      this.name = referenceName;
  }

  protected void preResolveCheck(final @NotNull SMP value) {
    if (!isReference)
      throw new IllegalStateException("Non-reference projection can't be resolved");
    if (isResolved())
      throw new IllegalStateException("Attempt to resolve already resolved reference: " + this.name);
    if (!value.isResolved())
      throw new IllegalArgumentException("Can't resolve model projection using non-resolved instance");
    if (!type().isAssignableFrom(value.type()))
      throw new IllegalStateException(String.format(
          "Value type '%s' is incompatible with reference type '%s'",
          value.type().name(),
          this.type().name()
      ));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull SMP value) {
    preResolveCheck(value);

    assert polymorphicTails == null || !polymorphicTails.isEmpty();

    setReferenceName(name);
    this.metaProjection = (MP) value.metaProjection();
    this.polymorphicTails = value.polymorphicTails();
    this.location = value.location();
    setNormalizedFrom(value.normalizedFrom);
    normalizedCache.putAll(((AbstractModelProjection<MP, SMP, M>) value).normalizedCache);
    this.isResolved = true;

    for (final Runnable callback : onResolvedCallbacks)
      callback.run();
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

  protected void assertResolved() throws UnresolvedReferenceException {
    if (!isResolved())
      throw new UnresolvedReferenceException(this);
  }

  @Override
  public @Nullable SMP normalizedFrom() { return normalizedFrom; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractModelProjection<?, ?, ?> that = (AbstractModelProjection<?, ?, ?>) o;
    return Objects.equals(model, that.model) &&
           Objects.equals(metaProjection, that.metaProjection);
  }

  @Override
  public @Nullable Throwable allocationTrace() { return allocationTrace; }

  @Override
  public int hashCode() {
//    assertResolved(); // todo: this should be prohibited for unresolved projections
    return Objects.hash(model, metaProjection);
  }

  @Override
  public String toString() {
    ProjectionReferenceName referenceName = referenceName();
    return referenceName == null ? String.format("<unnamed '%s' projection>", type().name()) : referenceName.toString();
  }
}
