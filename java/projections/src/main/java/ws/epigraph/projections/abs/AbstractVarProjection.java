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
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.VarNormalizationContext;
import ws.epigraph.projections.gen.*;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.types.TypeKind;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static ws.epigraph.projections.ProjectionUtils.findUniqueName;
import static ws.epigraph.projections.ProjectionUtils.linearizeVarTails;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractVarProjection<
    VP extends AbstractVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?, ?, ?>
    > implements GenVarProjection<VP, TP, MP> {

  private final @NotNull TypeApi type;
  private /*final*/ @Nullable ProjectionReferenceName name;
  private /*final @NotNull*/ @Nullable Map<String, TP> tagProjections;
  private /*final*/ boolean parenthesized;
  private /*final*/ @Nullable List<VP> polymorphicTails;

  private final @NotNull TextLocation location;

  private final List<Runnable> onResolvedCallbacks = new ArrayList<>();

  private final Map<TypeName, NormalizedCacheItem> normalizedCache = new ConcurrentHashMap<>();

  protected AbstractVarProjection(
      @NotNull TypeApi type,
      @NotNull Map<String, TP> tagProjections,
      boolean parenthesized,
      @Nullable List<VP> polymorphicTails,
      @NotNull TextLocation location) {

    this.type = type;
    this.tagProjections = tagProjections;
    this.parenthesized = parenthesized;
    this.polymorphicTails = polymorphicTails;
    this.location = location;

    validateTags();
    validateTails();

    // set model projection name in case of self-var
    if (type.kind() != TypeKind.ENTITY) {
      final TP tp = singleTagProjection();
      if (tp != null) {
        tp.projection().runOnResolved(
            () -> {
              if (name == null)
                name = tp.projection().referenceName();
            }
        );
      }
    }
  }

  /**
   * Creates an empty reference instance
   */
  protected AbstractVarProjection(@NotNull TypeApi type, @NotNull TextLocation location) {
    this.type = type;
    this.location = location;
  }

  private void validateTags() {
    assertResolved();
    assert tagProjections != null;

    if (!parenthesized && tagProjections().size() > 1)
      throw new IllegalArgumentException(
          String.format(
              "Non-parenthesized var projection can only contain one tag; was passed %d tags",
              tagProjections().size()
          ));

    for (final Map.Entry<String, TP> entry : tagProjections.entrySet()) {
      final String tagName = entry.getKey();

      final TP tagProjection = entry.getValue();
      final @NotNull DatumTypeApi tagType = tagProjection.tag().type();
      final DatumTypeApi tagProjectionModel = tagProjection.projection().type();

      if (!type.tagsMap().containsKey(tagName))
        throw new IllegalArgumentException(
            String.format("Tag '%s' does not belong to var type '%s'",
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

  private void validateTails() {
    final @Nullable List<VP> tails = polymorphicTails();
    if (tails != null) {
      for (final VP tail : tails) {
        if (tail.type().isAssignableFrom(type)) {
          throw new IllegalArgumentException(
              String.format(
                  "Tail type '%s' is assignable from var type '%s'. Tail defined at %s",
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
  public @NotNull TypeApi type() { return type; }

  @Override
  public @NotNull Map<String, TP> tagProjections() {
    assertResolved();
    assert tagProjections != null;

    return tagProjections;
  }

  public @Nullable TP tagProjection(@NotNull String tagName) { return tagProjections().get(tagName); }

  @Override
  public boolean parenthesized() {
    assertResolved();
    return parenthesized;
  }

  @Override
  public @Nullable List<VP> polymorphicTails() {
    assertResolved();
    return polymorphicTails;
  }

  protected abstract @NotNull VarNormalizationContext<VP> newNormalizationContext();

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull VP normalizedForType(@NotNull TypeApi targetType) {
    // keep in sync with AbstractModelProjection.normalizedForType
    assertResolved();
    assert tagProjections != null;
    assert (type().kind() == targetType.kind());

    if (targetType.equals(type()))
      return self();

    return VarNormalizationContext.withContext(
        this::newNormalizationContext,
        context -> {
          NormalizedCacheItem cacheItem = normalizedCache.get(targetType.name());
          // this projection is already being normalized. If we're in the same thread then it's OK to
          // return same (uninitialized) instance, it will get resolved higher in the stack
          // If this is another thread then we have to await for normalization to finish
          if (cacheItem != null)
            return cacheItem.allocatedByCurrentThread() ? cacheItem.vp : cacheItem.awaitForResolved();

          final List<VP> linearizedTails = linearizeVarTails(targetType, polymorphicTails());

          final TypeApi effectiveType = ProjectionUtils.mostSpecific(
              targetType,
              linearizedTails.isEmpty() ? type() : linearizedTails.get(0).type(),
              type()
          );

          if (effectiveType.equals(type()))
            return self();

          VP ref;
          ProjectionReferenceName normalizedRefName = null;
          if (name == null) {
            ref = context.newReference(effectiveType);
            normalizedCache.put(targetType.name(), new NormalizedCacheItem(ref));
          } else {
            ref = context.visited().get(name);

            if (ref != null)
              return ref;

            ref = context.newReference(effectiveType);
            context.visited().put(name, ref);

            normalizedRefName = ProjectionUtils.normalizedTailNamespace(
                name,
                effectiveType,
                ProjectionUtils.sameNamespace(type().name(), effectiveType.name())
            );
          }

          final List<VP> effectiveProjections = new ArrayList<>(linearizedTails);
          effectiveProjections.add(self()); //we're the least specific projection

          final List<VP> mergedTails = mergeTails(effectiveProjections);
          final List<VP> filteredMergedTails;
          if (mergedTails == null)
            filteredMergedTails = null;
          else {
            filteredMergedTails = mergedTails
                .stream()
                // remove 'uninteresting' tails which already describe `effectiveType`
                .filter(t -> (!t.type().isAssignableFrom(effectiveType)) && effectiveType.isAssignableFrom(t.type()))
//            .map(t -> t.normalizedForType(targetType))
                .collect(Collectors.toList());
          }

          List<VP> projectionsToMerge = effectiveProjections
              .stream()
              .filter(p -> p.type().isAssignableFrom(effectiveType))
              .collect(Collectors.toList());

          VP res = merge(effectiveType, true, filteredMergedTails, projectionsToMerge);

          ref.resolve(normalizedRefName, res);
          return ref;
        }
    );
  }

  private final class NormalizedCacheItem {
    final long creatorThreadId;
    final @NotNull VP vp;

    NormalizedCacheItem(final @NotNull VP vp) {
      creatorThreadId = Thread.currentThread().getId();
      this.vp = vp;
    }

    boolean allocatedByCurrentThread() { return Thread.currentThread().getId() == creatorThreadId; }

    @NotNull VP awaitForResolved() {
      assert !allocatedByCurrentThread();
      final CountDownLatch latch = new CountDownLatch(1);
      vp.runOnResolved(latch::countDown);
      try {
        latch.await();
      } catch (InterruptedException ignored) {
        Thread.currentThread().interrupt();
      }
      return vp;
    }
  }

  private @NotNull Map<String, TagApi> collectTags(final Iterable<? extends AbstractVarProjection<VP, TP, MP>> effectiveProjections) {
    Map<String, TagApi> tags = new LinkedHashMap<>();

    for (final AbstractVarProjection<VP, TP, MP> projection : effectiveProjections)
      projection.tagProjections().values()
          .stream()
          .map(GenTagProjectionEntry::tag)
          .forEach(t -> {if (!tags.containsKey(t.name())) tags.put(t.name(), t);});
    return tags;
  }

  @SuppressWarnings("unchecked")
  private @NotNull LinkedHashMap<String, TP> mergeTags(
      TypeApi effectiveType,
      boolean normalizeTags,
      final @NotNull Map<String, TagApi> tags,
      final @NotNull Iterable<? extends AbstractVarProjection<VP, TP, MP>> sources) {

    LinkedHashMap<String, TP> mergedTags = new LinkedHashMap<>();

    for (final TagApi tag : tags.values()) {
      List<TP> tagProjections = new ArrayList<>();
      for (final AbstractVarProjection<VP, TP, MP> projection : sources) {
        final @Nullable TP tagProjection = projection.tagProjection(tag.name());
        if (tagProjection != null) {
          if (normalizeTags) {
            final DatumTypeApi effectiveModelType = effectiveType.tagsMap().get(tag.name()).type();
            final DatumTypeApi minModelType = ProjectionUtils.mostSpecific(effectiveModelType, tag.type(), tag.type());
            final MP normalizedModel =
                (MP) tagProjection.projection().normalizedForType(minModelType);
            tagProjections.add(tagProjection.setModelProjection(normalizedModel));
          } else {
            tagProjections.add(tagProjection);
          }
        }
      }

      if (!tagProjections.isEmpty()) {
        final TP tp0 = tagProjections.get(0);

        @Nullable TP mergedTag = null;

        if (effectiveType.kind() != TypeKind.ENTITY) {
          if (tagProjections.size() == 1) {
            if (!tp0.projection().isResolved()) // recursive self-var
              mergedTag = tp0;
          } else {
            // todo: handle cases like `(foo $rec = ( foo $rec ) ~Bar ( foo ( baz ) ) )`
            // have to dereference and postpone merging?
            final Optional<TP> recTp = tagProjections.stream().filter(tp -> !tp.projection().isResolved()).findFirst();
            if (recTp.isPresent())
              throw new IllegalArgumentException(
                  String.format(
                      "Can't merge recursive and non-recursive projections [%s]",
                      recTp.get().projection().referenceName()
                  )
              );
          }
        }

        if (mergedTag == null)
          mergedTag = tp0.mergeTags(tag, tagProjections);

        if (mergedTag != null)
          mergedTags.put(tag.name(), mergedTag);
      }
    }
    return mergedTags;
  }

  private boolean mergeParenthesized(
      final @NotNull List<VP> varProjections,
      final @NotNull Map<String, TP> mergedTags) {

    return mergedTags.size() != 1 || varProjections.stream().anyMatch(GenVarProjection::parenthesized);
  }

  @Override
  public @NotNull VP merge(final @NotNull List<VP> varProjections) {
    return merge(type(), false, mergeTails(varProjections), varProjections);
  }

  protected @NotNull VP merge(
      final @NotNull TypeApi effectiveType,
      final boolean normalizeTags,
      final @Nullable List<VP> mergedTails,
      final @NotNull List<VP> varProjections) {

    if (varProjections.isEmpty()) throw new IllegalArgumentException("empty list of projections to merge");

    final @NotNull Map<String, TagApi> tags = collectTags(varProjections);

    final @NotNull Map<String, TP> mergedTags = mergeTags(effectiveType, normalizeTags, tags, varProjections);
    boolean mergedParenthesized = mergeParenthesized(varProjections, mergedTags);
    final ProjectionReferenceName mergedRefName = findUniqueName(varProjections, varProjections.get(0).location());
    VP res = merge(effectiveType, varProjections, mergedTags, mergedParenthesized, mergedTails);
    if (mergedRefName != null) res.setReferenceName(mergedRefName);
    return res;
  }

  private @Nullable List<VP> mergeTails(final @NotNull List<? extends AbstractVarProjection<VP, TP, MP>> sources) {

    Map<TypeApi, List<VP>> tailsByType = null;

    for (final AbstractVarProjection<VP, TP, MP> projection : sources) {
      final List<VP> tails = projection.polymorphicTails();
      if (tails != null) {
        if (tailsByType == null) tailsByType = new LinkedHashMap<>();
        for (VP tail : tails) {
          List<VP> collectedTails = tailsByType.computeIfAbsent(tail.type(), k -> new ArrayList<>());
          collectedTails.add(tail);
        }
      }
    }

    if (tailsByType == null) return null;

    List<VP> mergedTails = new ArrayList<>(tailsByType.size());

    for (final Map.Entry<TypeApi, List<VP>> entry : tailsByType.entrySet()) {
      mergedTails.add(
          merge(
              entry.getKey(),
              false,
              mergeTails(entry.getValue()),
              entry.getValue()
          )
      );
    }

    return mergedTails;
  }

  /* static */
  protected abstract VP merge(
      @NotNull TypeApi effectiveType,
      @NotNull List<VP> varProjections,
      @NotNull Map<String, TP> mergedTags,
      boolean mergedParenthesized,
      @Nullable List<VP> mergedTails);

  @SuppressWarnings("unchecked")
  private @NotNull VP self() { return (VP) this; }

  @Override
  public ProjectionReferenceName referenceName() { return name; }

  @Override
  public void setReferenceName(final @Nullable ProjectionReferenceName referenceName) {
    this.name = referenceName;
  }

  @Override
  public void resolve(@Nullable ProjectionReferenceName name, @NotNull VP value) {
    if (tagProjections != null)
      throw new IllegalStateException("Non-reference projection can't be resolved");
    if (isResolved())
      throw new IllegalStateException("Attempt to resolve already resolved reference: " + this.name);
    if (!value.isResolved())
      throw new IllegalArgumentException("Can't resolve var projection using non-resolved instance");
    if (!type().isAssignableFrom(value.type()))
      throw new IllegalStateException(String.format(
          "Value type '%s' is incompatible with reference type '%s'",
          value.type().name(),
          this.type().name()
      ));

    this.name = name;
    this.tagProjections = value.tagProjections();
    this.parenthesized = value.parenthesized();
    this.polymorphicTails = value.polymorphicTails();

//    System.out.println("Resolved " + name);
    for (final Runnable callback : onResolvedCallbacks) callback.run();
    onResolvedCallbacks.clear();

    // set model projection name in case of self-var
    if (type().kind() != TypeKind.ENTITY) {
      final TP tp = singleTagProjection();
      if (tp != null) {
        final MP mp = tp.projection();
        if (mp.referenceName() == null)
          mp.setReferenceName(name);
      }
    }
  }

  @Override
  public boolean isResolved() { return tagProjections != null; }

  @Override
  public void runOnResolved(final @NotNull Runnable callback) {
    if (isResolved())
      callback.run();
    else
      onResolvedCallbacks.add(callback);
  }

  protected void assertResolved() {
    if (!isResolved())
      throw new IllegalStateException(String.format(
          "Var projection for '%s' is not resolved: %s",
          type().name().toString(),
          name
      ));

    assert tagProjections != null;
  }

  @Override
  public @NotNull TextLocation location() {
    return location;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractVarProjection<?, ?, ?> that = (AbstractVarProjection<?, ?, ?>) o;

    //noinspection rawtypes
    return new GenProjectionsComparator().equals(this, that);

  }

  @Override
  public int hashCode() {
    if (name != null) return name.hashCode();
    return Objects.hash(type, tagProjections, polymorphicTails);
  }
}
