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

import static ws.epigraph.projections.ProjectionUtils.linearizeTails;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractVarProjection<
    VP extends AbstractVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?>
    > implements GenVarProjection<VP, TP, MP> {

  private final @NotNull Type type;
  private final @NotNull Map<String, TP> tagProjections;
  private final boolean parenthesized; // todo merge
  private final @Nullable List<VP> polymorphicTails;

  private int polymorphicDepth = -1;
  private final @NotNull TextLocation location;

  protected AbstractVarProjection(
      @NotNull Type type,
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
  }

  private void validateTags() {
    if (!parenthesized && tagProjections().size() != 1)
      throw new IllegalArgumentException(
          String.format(
              "Non-parenthesized var projection can only contain one tag; was passed %d tags",
              tagProjections().size()
          ));

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

  private void validateTails() {
    final @Nullable List<VP> tails = polymorphicTails();
    if (tails != null) {
      for (final VP tail : tails) {
        if (!type().isAssignableFrom(tail.type())) { // warn about useless cases when tail.type == type?
          throw new IllegalArgumentException(
              String.format(
                  "Tail type '%s' is not a sub-type of var type '%s'. Tail defined at %s",
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
  public @NotNull Type type() { return type; }

  @Override
  public @NotNull Map<String, TP> tagProjections() { return tagProjections; }

  public @Nullable TP tagProjection(@NotNull String tagName) { return tagProjections.get(tagName); }

  @Override
  public boolean parenthesized() { return parenthesized; }

  @Override
  public @Nullable List<VP> polymorphicTails() { return polymorphicTails; }

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

  @Override
  public @NotNull VP normalizedForType(final @NotNull Type targetType) {

    final @Nullable List<VP> polymorphicTails = polymorphicTails();
    if (polymorphicTails == null || polymorphicTails.isEmpty()) return self();

    final List<VP> linearizedTails = linearizeTails(targetType, polymorphicTails);

    if (linearizedTails.isEmpty())
      return self();

    final Type effectiveType = linearizedTails.get(0).type();

    final List<VP> effectiveProjections = new ArrayList<>(linearizedTails);
    final @Nullable List<VP> mergedTails = mergeTails(effectiveProjections);

    effectiveProjections.add(self()); //we're the least specific projection

    final Map<String, Type.Tag> tags = collectTags(effectiveProjections);
    final LinkedHashMap<String, TP> mergedTags = mergeTags(tags, effectiveProjections);

    final boolean mergedParenthesized = mergeParenthesized(effectiveProjections, mergedTags);

    return merge(effectiveType, effectiveProjections, mergedTags, mergedParenthesized, mergedTails);
  }

  private @NotNull Map<String, Type.Tag>
  collectTags(final Iterable<? extends AbstractVarProjection<VP, TP, MP>> effectiveProjections) {
    Map<String, Type.Tag> tags = new LinkedHashMap<>();

    for (final AbstractVarProjection<VP, TP, MP> projection : effectiveProjections)
      projection.tagProjections().values()
          .stream()
          .map(GenTagProjectionEntry::tag)
          .forEach(t -> {if (!tags.containsKey(t.name())) tags.put(t.name(), t);});
    return tags;
  }

  private @NotNull LinkedHashMap<String, TP> mergeTags(
      final @NotNull Map<String, Type.Tag> tags,
      final @NotNull Iterable<? extends AbstractVarProjection<VP, TP, MP>> sources) {

    LinkedHashMap<String, TP> mergedTags = new LinkedHashMap<>();

    for (final Type.Tag tag : tags.values()) {
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

  private boolean mergeParenthesized(
      final @NotNull List<VP> varProjections,
      final @NotNull Map<String, TP> mergedTags) {

    return mergedTags.size() != 1 || varProjections.stream().anyMatch(GenVarProjection::parenthesized);
  }

  private @Nullable List<VP> mergeTails(
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

  @Override
  public @NotNull VP merge(final @NotNull List<VP> varProjections) {
    if (varProjections.isEmpty()) throw new IllegalArgumentException("empty list of projections to merge");
    if (varProjections.size() == 1) return varProjections.get(0);

    final @NotNull Map<String, Type.Tag> tags = collectTags(varProjections);

    final @NotNull Map<String, TP> mergedTags = mergeTags(tags, varProjections);
    boolean mergedParenthesized = mergeParenthesized(varProjections, mergedTags);
    final @Nullable List<VP> mergedTails = mergeTails(varProjections);

    return merge(type(), varProjections, mergedTags, mergedParenthesized, mergedTails);
  }

  /* static */
  protected VP merge(
      final @NotNull Type type,
      final @NotNull List<VP> varProjections,
      final @NotNull Map<String, TP> mergedTags,
      final boolean mergedParenthesized,
      final @Nullable List<VP> mergedTails) {
    throw new RuntimeException("not implemented"); // todo make abstract
  }

  @SuppressWarnings("unchecked")
  private @NotNull VP self() { return (VP) this; }

  @Override
  public @NotNull TextLocation location() {
    return location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractVarProjection<?, ?, ?> that = (AbstractVarProjection<?, ?, ?>) o;
    return Objects.equals(type, that.type) &&
           Objects.equals(tagProjections, that.tagProjections) &&
           Objects.equals(polymorphicTails, that.polymorphicTails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, tagProjections, polymorphicTails);
  }
}
