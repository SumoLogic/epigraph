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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.names.TypeName;
import ws.epigraph.projections.NormalizationContext;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.UnresolvedReferenceException;
import ws.epigraph.projections.gen.GenProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.TypeApi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static ws.epigraph.projections.ProjectionUtils.buildReferenceName;
import static ws.epigraph.projections.ProjectionUtils.linearizeTails;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractProjection<
    P extends GenProjection<? extends P, TP, /*EP*/?, /*MP*/?>,
    SP extends AbstractProjection<P, SP, TP, /*EP*/?, /*MP*/?> /* & P */,
    TP extends AbstractTagProjectionEntry<TP, /*MP*/?>,
    EP extends AbstractEntityProjection<? extends P, EP, /*TP*/?, /*MP*/?> /* & P */,
    MP extends AbstractModelProjection<? extends P, /*EP*/?, /*TP*/?, /*MP*/?, ? extends MP, ?> /* & P */
    > implements GenProjection<SP, TP, EP, MP> {

  private final @NotNull TypeApi type;
  private /*final*/ @Nullable ProjectionReferenceName name;
  protected /*final*/ boolean flag;
  protected /*final @NotNull*/ @Nullable Map<String, TP> tagProjections;
  private /*final*/ @Nullable List<SP> polymorphicTails;

  private /*final*/ @NotNull TextLocation location;

  private boolean isResolved;
  private final boolean isReference;
  private final List<Runnable> onResolvedCallbacks = new ArrayList<>();

  private final Map<TypeName, NormalizedCacheItem> normalizedCache = new ConcurrentHashMap<>();
  protected final Map<TypeName, ProjectionReferenceName> normalizedTailNames = new ConcurrentHashMap<>();

  protected @Nullable SP normalizedFrom = null; // this = normalizedFrom ~ someType ?

  @SuppressWarnings("unchecked")
  protected AbstractProjection(
      @NotNull TypeApi type,
      boolean flag,
      @NotNull Map<String, TP> tagProjections,
      @Nullable List<SP> polymorphicTails,
      @NotNull TextLocation location) {

    assert polymorphicTails == null || !polymorphicTails.isEmpty();

    this.type = type;
    this.flag = flag;
    this.tagProjections = tagProjections;
    this.polymorphicTails = polymorphicTails;
    this.location = location;

    isResolved = true;
    isReference = false;

    validateTags();
    validateTails();
  }

  /**
   * Creates an empty reference instance
   */
  protected AbstractProjection(@NotNull TypeApi type, @NotNull TextLocation location) {
    this.type = type;
    this.location = location;

    isResolved = false;
    isReference = true;
  }

  @Override
  public boolean isResolved() {
    return isResolved;
  }

  protected void validateTags() {
    assertResolved();
    assert tagProjections != null;

    for (final Map.Entry<String, TP> entry : tagProjections.entrySet()) {
      final String tagName = entry.getKey();

      final TP tagProjection = entry.getValue();
      final @NotNull DatumTypeApi tagType = tagProjection.tag().type();
      final DatumTypeApi tagProjectionModel = tagProjection.modelProjection().type();

      if (!type.tagsMap().containsKey(tagName))
        throw new IllegalArgumentException(
            String.format("Tag '%s' does not belong to type '%s'",
                tagName, type.name()
            )
        );

      if (!tagType.isAssignableFrom(tagProjectionModel))
        throw new IllegalArgumentException(
            String.format("Tag '%s' projection type '%s' is not a subtype of tag type '%s'",
                tagName, tagProjectionModel.name(), tagType.name()
            )
        );

    }
  }

  protected void validateTails() {
    final @Nullable List<SP> tails = polymorphicTails();
    if (tails != null) {
      for (final SP tail : tails) {
        if (tail.type().isAssignableFrom(type)) {
          throw new IllegalArgumentException(
              String.format(
                  "Tail type '%s' is assignable from type '%s'. Tail defined at %s",
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
  public ProjectionReferenceName referenceName() { return name; }

  protected void assertResolved() throws UnresolvedReferenceException {
    if (!isResolved())
      throw new UnresolvedReferenceException(this);

    assert tagProjections != null;
  }

  @Override
  public @NotNull TypeApi type() { return type; }

  @Override
  public boolean flag() { return flag; }

  @Override
  public @NotNull Map<String, TP> tagProjections() {
//    assertResolved();
    assert tagProjections != null;

    return tagProjections;
  }

  public @Nullable TP tagProjection(@NotNull String tagName) { return tagProjections().get(tagName); }

  @Override
  public @Nullable List<SP> polymorphicTails() {
    assertResolved();
    return polymorphicTails;
  }

  @Override
  public void setNormalizedTailReferenceName(
      @NotNull TypeApi type,
      @NotNull ProjectionReferenceName tailReferenceName) {

    normalizedTailNames.put(type.name(), tailReferenceName);
  }

  @Override
  public void setReferenceName(final @Nullable ProjectionReferenceName referenceName) {
    this.name = referenceName;
  }

  public void copyNormalizedTailReferenceNames(@NotNull SP vp) {
    normalizedTailNames.putAll(vp.normalizedTailNames);
  }

  protected abstract @NotNull NormalizationContext<TypeApi, P> newNormalizationContext();

  @Contract(pure = true)
  @SuppressWarnings("unchecked")
  protected @NotNull SP self() { return (SP) this; }

  @SuppressWarnings("unchecked")
  @Override
  public @NotNull SP normalizedForType(final @NotNull TypeApi targetType) {
    ProjectionReferenceName resultReferenceName = normalizedTailNames.get(targetType.name());

    if (targetType.equals(type()))
      return self();

    assertResolved();
    assert tagProjections != null;
    assert (type().kind() == targetType.kind());

    return NormalizationContext.withContext(
        this::newNormalizationContext,
        context -> {
          TypeName targetTypeName = targetType.name();
          NormalizedCacheItem cacheItem = normalizedCache.get(targetTypeName);
          // this projection is already being normalized. If we're in the same thread then it's OK to
          // return same (uninitialized) instance, it will get resolved higher in the stack
          // If this is another thread then we have to await for normalization to finish
          if (cacheItem != null)
            return cacheItem.allocatedByCurrentThread() ? cacheItem.p : cacheItem.awaitForResolved();

          final List<SP> linearizedTails = linearizeTails(targetType, polymorphicTails());

          final TypeApi effectiveType = ProjectionUtils.mostSpecific(
              targetType,
              linearizedTails.isEmpty() ? type() : linearizedTails.get(0).type(),
              type()
          );

          if (effectiveType.equals(type()))
            return self();

          try {
            SP ref;
            ProjectionReferenceName normalizedRefName = resultReferenceName;
            if (this.name == null) {
              ref = (SP) context.newReference(effectiveType, (P) self());
              ref.setReferenceName(normalizedRefName);
            } else {
              NormalizationContext.VisitedKey visitedKey =
                  new NormalizationContext.VisitedKey(this.name, effectiveType.name());
              ref = (SP) context.visited().get(visitedKey);

              if (ref != null)
                return ref;

              ref = (SP) context.newReference(effectiveType, (P) self());
              context.visited().put(visitedKey, (P) ref);

              if (normalizedRefName == null)
                normalizedRefName = ProjectionUtils.normalizedTailNamespace(
                    name,
                    effectiveType,
                    ProjectionUtils.sameNamespace(type().name(), effectiveType.name())
                );
            }
            normalizedCache.put(targetTypeName, new NormalizedCacheItem(ref));

            final List<SP> effectiveProjections = new ArrayList<>(linearizedTails);
            effectiveProjections.add(self()); //we're the least specific projection

            final List<SP> mergedTails = mergeTails(effectiveProjections);

            final List<SP> filteredMergedTails;
            if (mergedTails == null)
              filteredMergedTails = null;
            else {
              final List<SP> tmp = mergedTails
                  .stream()
                  // remove 'uninteresting' tails which already describe `effectiveType`
                  .filter(t -> (!t.type().isAssignableFrom(effectiveType)) && effectiveType.isAssignableFrom(t.type()))
                  //            .map(t -> t.normalizedForType(targetType))
                  .collect(Collectors.toList());
              filteredMergedTails = tmp.isEmpty() ? null : tmp;
            }

            List<SP> projectionsToMerge = effectiveProjections
                .stream()
                .filter(p -> p.type().isAssignableFrom(effectiveType))
                .collect(Collectors.toList());

            SP res = merge(effectiveType, true, filteredMergedTails, projectionsToMerge, normalizedRefName);
            res = postNormalizedForType(targetType, res);
            res.normalizedTailNames.putAll(normalizedTailNames);
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

  /**
   * Called after {@code normalizeForType} is performed. Can do some extra steps and return a modified version.
   */
  protected @NotNull SP postNormalizedForType(@NotNull TypeApi targetType, @NotNull SP normalizationResult) {
    return normalizationResult;
  }


  @SuppressWarnings("unchecked")
  @Override
  public @NotNull SP merge(@NotNull TypeApi type, @NotNull List<SP> projections) {
    assert !projections.isEmpty();

    projections = new ArrayList<>(new LinkedHashSet<>(projections)); // dedup

    if (projections.size() == 1) {
      return projections.get(0);
    } else {
      Optional<SP> unresolvedOpt = projections.stream().filter(p -> !p.isResolved()).findFirst();
      if (unresolvedOpt.isPresent()) {
        String message = NormalizationContext.withContext(
            this::newNormalizationContext,
            context -> {
              SP origin = (SP) context.origin((P) unresolvedOpt.get());

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
        return merge(type, false, mergeTails(projections), projections, null);
    }
  }

  protected SP merge(
      final @NotNull TypeApi effectiveType,
      final boolean normalizeTags,
      final @Nullable List<SP> mergedTails,
      final @NotNull List<SP> projections,
      final @Nullable ProjectionReferenceName defaultReferenceName) {

    if (projections.isEmpty()) throw new IllegalArgumentException("empty list of projections to merge");

    final ProjectionReferenceName mergedRefName = defaultReferenceName == null
                                                  ? buildReferenceName(projections, projections.get(0).location())
                                                  : defaultReferenceName;

    boolean mergedFlag = projections.stream().anyMatch(GenProjection::flag);

    SP res = merge(effectiveType, projections, normalizeTags, mergedFlag, mergedTails);
    if (mergedRefName != null) res.setReferenceName(mergedRefName);

    // todo check for clashes
    Map<TypeName, ProjectionReferenceName> mergedTailNames = new HashMap<>();
    for (SP vp : projections) {
      mergedTailNames.putAll(vp.normalizedTailNames);
    }
    res.normalizedTailNames.putAll(mergedTailNames);

    return res;
  }

  @SuppressWarnings("unchecked")
  private @Nullable List<SP> mergeTails(final @NotNull List<? extends SP> sources) {

    Map<TypeApi, List<SP>> tailsByType = null;

    for (final SP projection : sources) {
      final List<SP> tails = projection.polymorphicTails();
      if (tails != null) {
        if (tailsByType == null) tailsByType = new LinkedHashMap<>();
        for (SP tail : tails) {
          List<SP> collectedTails = tailsByType.computeIfAbsent(tail.type(), k -> new ArrayList<>());
          collectedTails.add(tail);
        }
      }
    }

    if (tailsByType == null) return null;

    List<SP> mergedTails = new ArrayList<>(tailsByType.size());

    for (final Map.Entry<TypeApi, List<SP>> entry : tailsByType.entrySet()) {
      mergedTails.add(
          merge(
              entry.getKey(),
              false,
              mergeTails(entry.getValue()),
              entry.getValue(),
              null
          )
      );
    }

    return mergedTails;
  }

  /* static */
  protected abstract SP merge(
      @NotNull TypeApi effectiveType,
      @NotNull List<SP> projections,
      boolean normalizeTags,
      boolean mergedFlag,
      @Nullable List<SP> mergedTails);

  protected void preResolveCheck(final @NotNull SP value) {
    if (!isReference)
      throw new IllegalStateException("Non-reference projection can't be resolved");
    if (isResolved())
      throw new IllegalStateException("Attempt to resolve an already resolved reference: " + this.name);
    if (!value.isResolved())
      throw new IllegalArgumentException("Can't resolve projection using non-resolved instance");
    if (!type().isAssignableFrom(value.type()))
      throw new IllegalStateException(String.format(
          "Value type '%s' is incompatible with reference type '%s'",
          value.type().name(),
          this.type().name()
      ));
  }

  @Override
  public void resolve(@Nullable ProjectionReferenceName name, @NotNull SP value) {
    preResolveCheck(value);

    assert polymorphicTails == null || !polymorphicTails.isEmpty();

    this.name = name;
    this.flag = value.flag;
    this.polymorphicTails = value.polymorphicTails();
    this.location = value.location();
    this.normalizedFrom = value.normalizedFrom();
    this.normalizedTailNames.putAll(value.normalizedTailNames);
    this.isResolved = true;

//    System.out.println("Resolved " + name);
    for (final Runnable callback : onResolvedCallbacks)
      callback.run();
    onResolvedCallbacks.clear();
  }

  @Override
  public void runOnResolved(final @NotNull Runnable callback) {
    if (isResolved())
      callback.run();
    else
      onResolvedCallbacks.add(callback);
  }

  @Override
  public @Nullable SP normalizedFrom() { return normalizedFrom; }

  @Override
  public @NotNull TextLocation location() { return location; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final AbstractProjection<?, ?, ?, ?, ?> that = (AbstractProjection<?, ?, ?, ?, ?>) o;
    return flag == that.flag &&
           Objects.equals(type, that.type) &&
           Objects.equals(polymorphicTails, that.polymorphicTails);
  }

  @Override
  public int hashCode() {
//    assertResolved(); // todo: this should be prohibited for unresolved projections

    return Objects.hash(type, flag, polymorphicTails);
  }

  @Override
  public String toString() {
    ProjectionReferenceName referenceName = referenceName();
    return referenceName == null ? String.format("<unnamed '%s' projection>", type().name()) : referenceName.toString();
  }

  private final class NormalizedCacheItem {
    final long creatorThreadId;
    final @NotNull SP p;

    NormalizedCacheItem(final @NotNull SP p) {
      creatorThreadId = Thread.currentThread().getId();
      this.p = p;
    }

    boolean allocatedByCurrentThread() { return Thread.currentThread().getId() == creatorThreadId; }

    @NotNull SP awaitForResolved() {
      assert !allocatedByCurrentThread();
      final CountDownLatch latch = new CountDownLatch(1);
      p.runOnResolved(latch::countDown);
      try {
        latch.await();
      } catch (InterruptedException ignored) {
        Thread.currentThread().interrupt();
      }
      return p;
    }
  }
}
