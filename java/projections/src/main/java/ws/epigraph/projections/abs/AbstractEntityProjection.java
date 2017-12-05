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
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenEntityProjection;
import ws.epigraph.projections.gen.GenProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.types.TypeKind;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractEntityProjection<
    P extends GenProjection<? extends P, TP, EP, ? extends MP>,
    EP extends AbstractEntityProjection<P, EP, TP, MP>,
    TP extends AbstractTagProjectionEntry<TP, MP>,
    MP extends AbstractModelProjection<P, EP, /*TP*/?, /*MP*/?, ? extends MP, ?>
    >
    extends AbstractProjection<P, EP, TP, EP, MP>
    implements GenEntityProjection<EP, TP, MP> {

  private /*final*/ boolean parenthesized;

  @SuppressWarnings("unchecked")
  protected AbstractEntityProjection(
      @NotNull TypeApi type,
      boolean flag,
      @NotNull Map<String, TP> tagProjections,
      boolean parenthesized,
      @Nullable List<EP> polymorphicTails,
      @NotNull TextLocation location) {

    super(type, flag, tagProjections, polymorphicTails, location);

    if (type.kind() != TypeKind.ENTITY)
      throw new IllegalArgumentException("Entity projection can't be created for non-entity type " + type.name());

    this.parenthesized = parenthesized;

    validateParenthesized();
  }

  private void validateParenthesized() {
    if (!parenthesized && tagProjections().size() > 1)
      throw new IllegalArgumentException(
          String.format(
              "Non-parenthesized entity projection can only contain one tag; was passed %d tags",
              tagProjections().size()
          ));
  }

  /**
   * Creates an empty reference instance
   */
  protected AbstractEntityProjection(@NotNull TypeApi type, @NotNull TextLocation location) {
    super(type, location);

    if (type.kind() != TypeKind.ENTITY)
      throw new IllegalArgumentException("Entity projection can't be created for non-entity type " + type.name());
  }

  public boolean isPathEnd() {
    assertResolved();
    return tagProjections().isEmpty();
  }

  @Override
  public boolean parenthesized() {
    assertResolved();
    return parenthesized;
  }

  @SuppressWarnings("unchecked")
  private @NotNull LinkedHashMap<String, TP> mergeTags(
      TypeApi effectiveType,
      boolean normalizeTags,
      @NotNull Iterable<TagApi> tags,
      @NotNull Iterable<? extends EP> sources) {

    LinkedHashMap<String, TP> mergedTags = new LinkedHashMap<>();

    for (TagApi tag : tags) {
      // collect tag projections from all sources
      List<TP> tagProjections = new ArrayList<>();
      for (EP source : sources) {
        @Nullable TP sourceTp = source.tagProjection(tag.name());
        if (sourceTp != null) {
          if (normalizeTags) {
            DatumTypeApi effectiveModelType = effectiveType.tagsMap().get(tag.name()).type();
            DatumTypeApi minModelType = ProjectionUtils.mostSpecific(effectiveModelType, tag.type(), tag.type());

            MP mp = sourceTp.modelProjection();
            MP normalizedModel = mp.normalizedForType(minModelType);

            tagProjections.add(sourceTp.setModelProjection(normalizedModel));
          } else {
            tagProjections.add(sourceTp);
          }
        }
      }

      // merge them into one
      if (!tagProjections.isEmpty()) {
        final TP tp0 = tagProjections.get(0);

        @Nullable TP mergedTag = null;

//        if (effectiveType.kind() != TypeKind.ENTITY) {
//          if (tagProjections.size() == 1) {
//            if (!tp0.modelProjection().isResolved()) // recursive self-var
//              mergedTag = tp0;
//          } else {
//            // todo: handle cases like `(foo $rec = ( foo $rec ) ~Bar ( foo ( baz ) ) )`
//            // have to dereference and postpone merging?
//            // see also OpOutputProjectionsTest::testNormalizeRecursiveList2
//            final Optional<TP> recTp =
//                tagProjections.stream().filter(tp -> !tp.modelProjection().isResolved()).findFirst();
//            recTp.map(tp -> {
//              throw new IllegalArgumentException(
//                  String.format(
//                      "Can't merge recursive projection [%s] with other projections (%s)",
//                      tp.modelProjection().referenceName(),
//                      tagProjections.stream()
//                          .map(x -> x.tag().name() + ":" + x.modelProjection().toString())
//                          .collect(Collectors.joining(", "))
//                  )
//              );
//            });
//          }
//        }

        if (mergedTag == null)
          mergedTag = tp0.mergeTags(tag, tagProjections);

        if (mergedTag != null)
          mergedTags.put(tag.name(), mergedTag);
      }
    }

    return mergedTags;
  }

  private @NotNull Map<String, TagApi> collectTags(
      @NotNull Iterable<? extends EP> effectiveProjections) {

    Map<String, TagApi> tags = new LinkedHashMap<>();

    for (final EP projection : effectiveProjections)
      projection.tagProjections().values()
          .stream()
          .map(GenTagProjectionEntry::tag)
          .forEach(t -> {if (!tags.containsKey(t.name())) tags.put(t.name(), t);});
    return tags;
  }


  private boolean mergeParenthesized(
      final @NotNull List<EP> varProjections,
      final @NotNull Map<String, TP> mergedTags) {

    return mergedTags.size() != 1 || varProjections.stream().anyMatch(GenEntityProjection::parenthesized);
  }

  @Override
  protected EP merge(
      final @NotNull TypeApi effectiveType,
      final @NotNull List<EP> projections,
      final boolean normalizeTags,
      final boolean mergedFlag,
      final @Nullable List<EP> mergedTails) {

    final @NotNull Map<String, TagApi> tags = collectTags(projections);
    final @NotNull Map<String, TP> mergedTags = mergeTags(effectiveType, normalizeTags, tags.values(), projections);

    return merge(
        effectiveType,
        projections,
        mergedFlag,
        mergedTags,
        mergeParenthesized(projections, mergedTags),
        mergedTails
    );
  }

  /* static */
  protected abstract EP merge(
      @NotNull TypeApi effectiveType,
      @NotNull List<EP> projections,
      boolean mergedFlag,
      @NotNull Map<String, TP> mergedTags,
      boolean mergedParenthesized,
      @Nullable List<EP> mergedTails);

  @Override
  public void resolve(@Nullable ProjectionReferenceName name, @NotNull EP value) {
    preResolveCheck(value);
    this.parenthesized = value.parenthesized();
    this.tagProjections = value.tagProjections;
    super.resolve(name, value);
    validateTags();
  }

  @Override
  public boolean equals(final Object o) {
    if (!super.equals(o)) return false;
    AbstractEntityProjection<?, ?, ?, ?> that = (AbstractEntityProjection<?, ?, ?, ?>) o;
    return Objects.equals(tagProjections, that.tagProjections);
  }

  @Override
  public int hashCode() {
    return super.hashCode() * 31 + Objects.hashCode(tagProjections);
  }
}
