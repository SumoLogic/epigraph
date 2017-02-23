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
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeApi;

import java.util.*;
import java.util.stream.Collectors;

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
  private final @NotNull Map<String, TP> tagProjections;
  private final boolean parenthesized; // todo merge
  private final @Nullable List<VP> polymorphicTails;

  private final @NotNull TextLocation location;

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
      final @NotNull DatumTypeApi tagType = tagProjection.tag().type();
      final DatumTypeApi tagProjectionModel = tagProjection.projection().model();

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
  public @NotNull TypeApi type() { return type; }

  @Override
  public @NotNull Map<String, TP> tagProjections() { return tagProjections; }

  public @Nullable TP tagProjection(@NotNull String tagName) { return tagProjections.get(tagName); }

  @Override
  public boolean parenthesized() { return parenthesized; }

  @Override
  public @Nullable List<VP> polymorphicTails() { return polymorphicTails; }

  @Override
  public @NotNull VP normalizedForType(final @NotNull TypeApi targetType) {

    final List<VP> linearizedTails = linearizeVarTails(targetType, polymorphicTails());

    final TypeApi effectiveType = ProjectionUtils.mostSpecific(
        targetType,
        linearizedTails.isEmpty() ? this.type() : linearizedTails.get(0).type()
    );
//    final TypeApi effectiveType = linearizedTails.isEmpty() ? this.type() : linearizedTails.get(0).type();

    final List<VP> effectiveProjections = new ArrayList<>(linearizedTails);
//    final List<VP> effectiveProjections = linearizedTails.stream()
//        .map(t -> t.normalizedForType(effectiveType)).collect(Collectors.toList());

    // before adding self to effectiveProjections!
    final List<VP> mergedTails = mergeTails(effectiveProjections);
    final List<VP> mergedNormalizedTails = mergedTails == null ? null : mergedTails
        .stream()
        .filter(t -> !t.type()
            .isAssignableFrom(effectiveType)) // remove 'uninteresting' tails that aren't specific enough
        .map(t -> t.normalizedForType(targetType))
        .collect(Collectors.toList());

    effectiveProjections.add(self()); //we're the least specific projection

    return merge(effectiveType, true, mergedNormalizedTails, effectiveProjections);
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
            final DatumTypeApi minModelType = ProjectionUtils.mostSpecific(effectiveModelType, tag.type());
            final MP normalizedModel = (MP) tagProjection.projection().normalizedForType(minModelType);
            tagProjections.add(tagProjection.setModelProjection(normalizedModel));
          } else {
            tagProjections.add(tagProjection);
          }
        }
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
    return merge(effectiveType, varProjections, mergedTags, mergedParenthesized, mergedTails);
  }

  private @Nullable List<VP> mergeTails(final @NotNull List<? extends AbstractVarProjection<VP, TP, MP>> sources) {

    Map<TypeApi, List<VP>> tailsByType = null;

    for (final AbstractVarProjection<VP, TP, MP> projection : sources) {
      final List<VP> tails = projection.polymorphicTails();
      if (tails != null) {
        if (tailsByType == null) tailsByType = new HashMap<>();
        for (VP tail : tails) {
          List<VP> collectedTails = tailsByType.computeIfAbsent(tail.type(), k -> new ArrayList<>());
          collectedTails.add(tail);
        }
      }
    }

    if (tailsByType == null) return null;

    List<VP> mergedTails = new ArrayList<>(tailsByType.size());

    for (final Map.Entry<TypeApi, List<VP>> entry : tailsByType.entrySet()) {
      mergedTails.add(merge(entry.getKey(), false, mergeTails(entry.getValue()), entry.getValue()));
    }

    return mergedTails;
  }


  /* static */
  protected VP merge(
      final @NotNull TypeApi effectiveType,
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
