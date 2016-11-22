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

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

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
  public VP normalizedForType(@NotNull final Type targetType, @NotNull Type fallbackType, @NotNull List<VP> varProjections) {

    final Stream<AbstractVarProjection<VP, TP, MP>> allTails =
        varProjections.stream()
                      .flatMap(v -> {
                        List<VP> x = v.polymorphicTails();
                        return x == null
                               ? Stream.empty()
                               : x.stream();
                      });

    final Deque<AbstractVarProjection<VP, TP, MP>> linearizedTails = linearizeTails(targetType, allTails);

    final Type effectiveType = linearizedTails.isEmpty() ? fallbackType : linearizedTails.getFirst().type();

    List<AbstractVarProjection<VP, TP, MP>> effectiveProjections = new ArrayList<>(linearizedTails);
    effectiveProjections.addAll(varProjections); // remove from wiki too?
//    Collection<AbstractVarProjection<VP, TP, MP>> effectiveProjections = linearizedTails;

    // collect all tags in proper order
    Set<Type.Tag> tags = new LinkedHashSet<>();

    for (final AbstractVarProjection<VP, TP, MP> projection : effectiveProjections)
      projection.tagProjections().values().stream().map(GenTagProjectionEntry::tag).forEach(tags::add);

    LinkedHashMap<String, TP> mergedTags = new LinkedHashMap<>();

    for (final Type.Tag tag : tags) {
      List<TP> tagProjections = new ArrayList<>();
      for (final AbstractVarProjection<VP, TP, MP> projection : effectiveProjections) {
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

    return mergeNoTails(effectiveType, mergedTags, varProjections);
  }

  @NotNull
  private Deque<AbstractVarProjection<VP, TP, MP>> linearizeTails(
      @NotNull Type t,
      @NotNull Stream<? extends AbstractVarProjection<VP, TP, MP>> tails) {

//    if (tails.isEmpty()) return Collections.emptyList();
//    if (tails.size() == 1) {
//      if (t.isAssignableFrom(tails.get(0).type()))
//        return tails;
//      else
//        return Collections.emptyList();
//    } else {
    return linearizeTails(t, tails, new LinkedList<>());
//    }
  }

  @NotNull
  private Deque<AbstractVarProjection<VP, TP, MP>> linearizeTails(
      @NotNull Type type,
      @NotNull Stream<? extends AbstractVarProjection<VP, TP, MP>> tails,
      @NotNull Deque<AbstractVarProjection<VP, TP, MP>> linearizedTails) {

    final Optional<? extends AbstractVarProjection<VP, TP, MP>> matchingTailOpt =
        tails.filter(tail -> tail.type().isAssignableFrom(type)).findFirst();

    if (matchingTailOpt.isPresent()) {
      final AbstractVarProjection<VP, TP, MP> matchingTail = matchingTailOpt.get();
      linearizedTails.addFirst(stripTails(matchingTail));

      final List<VP> tails2 = matchingTail.polymorphicTails();
      if (tails2 != null)
        linearizeTails(type, tails2.stream(), linearizedTails);

    }

    return linearizedTails;
  }

  @NotNull
  /* static */ protected VP mergeNoTails(
      @NotNull Type type,
      @NotNull LinkedHashMap<String, TP> mergedTags,
      @NotNull List<VP> varProjections) {
    throw new RuntimeException("not implemented"); // todo make abstract
  }

  @NotNull
  protected AbstractVarProjection<VP, TP, MP> stripTails(@NotNull AbstractVarProjection<VP, TP, MP> vp) {
    return new AbstractVarProjection<VP, TP, MP>(
        vp.type(),
        vp.tagProjections(),
        Collections.emptyList(),
        vp.location()
    ) {};
  }

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
