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
import ws.epigraph.projections.gen.GenEntityProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.TypeApi;
import ws.epigraph.types.TypeKind;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractEntityProjection<
    EP extends AbstractEntityProjection<EP, TP, MP>,
    TP extends AbstractTagProjectionEntry<TP, MP>,
    MP extends AbstractModelProjection<EP, /*TP*/?, /*MP*/?, ? extends MP, ?>
    >
    extends AbstractProjection<EP, TP, EP, MP>
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

    if (tagProjections.size() > 1 && !parenthesized)
      throw new IllegalArgumentException("'parenthesized' must be 'true' for a multi-tag projection");
  }

  /**
   * Creates an empty reference instance
   */
  protected AbstractEntityProjection(@NotNull TypeApi type, @NotNull TextLocation location) {
    super(type, location);
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

  private boolean mergeParenthesized(
      final @NotNull List<EP> varProjections,
      final @NotNull Map<String, TP> mergedTags) {

    return mergedTags.size() != 1 || varProjections.stream().anyMatch(GenEntityProjection::parenthesized);
  }

  @Override
  protected EP merge(
      final @NotNull TypeApi effectiveType,
      final @NotNull List<EP> projections,
      final boolean mergedFlag,
      final @NotNull Map<String, TP> mergedTags,
      final @Nullable List<EP> mergedTails) {

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
    super.resolve(name, value);
  }

  @Override
  protected void validateTags() {
    super.validateTags();

    if (!parenthesized && tagProjections().size() > 1)
      throw new IllegalArgumentException(
          String.format(
              "Non-parenthesized entity projection can only contain one tag; was passed %d tags",
              tagProjections().size()
          ));
  }
}
