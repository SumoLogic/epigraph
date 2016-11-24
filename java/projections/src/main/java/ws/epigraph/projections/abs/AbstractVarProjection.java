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
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractVarProjection<
    VP extends AbstractVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?>
    > implements GenVarProjection<VP, TP, MP> {

  @NotNull
  private final Type type;
  @NotNull
  private final Map<String, TP> tagProjections;
  @Nullable
  private final List<VP> polymorphicTails;

  private int polymorphicDepth = -1;
  @NotNull
  private final TextLocation location;

  public AbstractVarProjection(
      @NotNull Type type,
      @NotNull Map<String, TP> tagProjections,
      @Nullable List<VP> polymorphicTails,
      @NotNull TextLocation location) {
    this.type = type;
    this.tagProjections = tagProjections;
    this.polymorphicTails = polymorphicTails;
    this.location = location;

    validateTags();
    // todo validate tails (should be subtypes of `type`)
  }

  private void validateTags() {
    for (final Map.Entry<String, TP> entry : tagProjections.entrySet()) {
      final String tagName = entry.getKey();

      final TP tagProjection = entry.getValue();
      final @NotNull DatumType tagType = tagProjection.tag().type;
      final DatumType tagProjectionModel = tagProjection.projection().model();

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

  @NotNull
  public Type type() { return type; }

  @NotNull
  public Map<String, TP> tagProjections() { return tagProjections; }

  @Nullable
  public TP tagProjection(@NotNull String tagName) { return tagProjections.get(tagName); }

  @Nullable
  public List<VP> polymorphicTails() { return polymorphicTails; }

  /**
   * Max polymorphic tail depth.
   */
  public int polymorphicDepth() {
    if (polymorphicDepth == -1)
      polymorphicDepth = polymorphicTails == null
                         ? 0
                         : polymorphicTails.stream()
                                           .mapToInt(AbstractVarProjection::polymorphicDepth)
                                           .max()
                                           .orElse(0);
    return polymorphicDepth;
  }

  @NotNull
  @Override
  public VP normalizedForType(@NotNull final Type targetType) {

    @Nullable final List<VP> polymorphicTails = polymorphicTails();
    if (polymorphicTails == null || polymorphicTails.isEmpty()) return self();

    final List<VP> linearizedTails = linearizeTails(targetType, polymorphicTails);

    if (linearizedTails.isEmpty())
      return stripTails(self());

    final Type effectiveType = linearizedTails.get(0).type();

    final List<VP> effectiveProjections = new ArrayList<>(linearizedTails);
    effectiveProjections.add(self()); //we're the least specific projection

    final Set<Type.Tag> tags = collectTags(effectiveProjections);
    final LinkedHashMap<String, TP> mergedTags = mergeTags(tags, effectiveProjections);

    return merge(effectiveType, effectiveProjections, mergedTags, null);
  }

  @NotNull
  private Set<Type.Tag> collectTags(final List<? extends AbstractVarProjection<VP, TP, MP>> effectiveProjections) {
    Set<Type.Tag> tags = new LinkedHashSet<>();

    for (final AbstractVarProjection<VP, TP, MP> projection : effectiveProjections)
      projection.tagProjections().values().stream().map(GenTagProjectionEntry::tag).forEach(tags::add);
    return tags;
  }

  @NotNull
  private LinkedHashMap<String, TP> mergeTags(
      final @NotNull Set<Type.Tag> tags,
      final @NotNull List<? extends AbstractVarProjection<VP, TP, MP>> sources) {

    LinkedHashMap<String, TP> mergedTags = new LinkedHashMap<>();

    for (final Type.Tag tag : tags) {
      List<TP> tagProjections = new ArrayList<>();
      for (final AbstractVarProjection<VP, TP, MP> projection : sources) {
        final @Nullable TP tagProjection = projection.tagProjection(tag.name());
        if (tagProjection != null)
          tagProjections.add(tagProjection);
      }

      if (!tagProjections.isEmpty()) {
        final @Nullable TP mergedTag = tagProjections.get(0).mergeTags(tag, tagProjections);
        if (mergedTag != null)
          mergedTags.put(tag.name(), mergedTag);
      }
    }
    return mergedTags;
  }

  @Nullable
  private List<VP> mergeTails(
      final @NotNull List<? extends AbstractVarProjection<VP, TP, MP>> sources) {

    List<VP> mergedTails = null;

    for (final AbstractVarProjection<VP, TP, MP> projection : sources) {
      final List<VP> tails = projection.polymorphicTails();
      if (tails != null) {
        if (mergedTails == null) mergedTails = new ArrayList<>();
        mergedTails.addAll(tails);
      }
    }

    return mergedTails;
  }

  @NotNull
  private List<VP> linearizeTails(@NotNull Type t, @NotNull List<VP> tails) {

    if (tails.isEmpty()) return Collections.emptyList();
    if (tails.size() == 1) {
      final VP tail = tails.get(0);
      final List<VP> tailTails = tail.polymorphicTails();

      if (tail.type().isAssignableFrom(t)) {
        if (tailTails == null || tailTails.isEmpty())
          return tails;
        // else run full linearizeTails below
      } else
        return Collections.emptyList();
    }

    return linearizeTails(t, tails, new LinkedList<>());
  }

  @NotNull
  private List<VP> linearizeTails(
      @NotNull Type type,
      @NotNull List<VP> tails,
      @NotNull LinkedList<VP> linearizedTails) {

    final Optional<VP> matchingTailOpt = tails.stream().filter(tail -> tail.type().isAssignableFrom(type)).findFirst();

    if (matchingTailOpt.isPresent()) {
      final VP matchingTail = matchingTailOpt.get();
      linearizedTails.addFirst(stripTails(matchingTail));

      final List<VP> tails2 = matchingTail.polymorphicTails();
      if (tails2 != null)
        linearizeTails(type, tails2, linearizedTails);

    }

    return linearizedTails;
  }

  @NotNull
  @Override
  public VP merge(@NotNull final List<VP> varProjections) {
    if (varProjections.isEmpty()) throw new IllegalArgumentException("empty list of projections to merge");
    if (varProjections.size() == 1) return varProjections.get(0);

    @NotNull final Set<Type.Tag> tags = collectTags(varProjections);

    @NotNull final Map<String, TP> mergedTags = mergeTags(tags, varProjections);
    @Nullable final List<VP> mergedTails = mergeTails(varProjections);

    return merge(type(), varProjections, mergedTags, mergedTails);
  }

  /* static */
  protected VP merge(
      final @NotNull Type type,
      final @NotNull List<VP> varProjections,
      final @NotNull Map<String, TP> mergedTags,
      final @Nullable List<VP> mergedTails) {
    throw new RuntimeException("not implemented"); // todo make abstract
  }

  @NotNull
  protected VP stripTails(@NotNull VP vp) {
    return merge(vp.type(), Collections.singletonList(vp), vp.tagProjections(), null);
  }

  @SuppressWarnings("unchecked")
  @NotNull
  private VP self() { return (VP) this; }

  @NotNull
  public TextLocation location() {
    return location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractVarProjection that = (AbstractVarProjection) o;
    return Objects.equals(type, that.type) &&
           Objects.equals(tagProjections, that.tagProjections) &&
           Objects.equals(polymorphicTails, that.polymorphicTails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, tagProjections, polymorphicTails);
  }
}
