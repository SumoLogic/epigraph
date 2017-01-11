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
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.TagApi;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractTagProjectionEntry<
    TP extends AbstractTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?, ?>
    > implements GenTagProjectionEntry<TP, MP> {

  private final @NotNull TagApi tag;
  private final @NotNull MP projection;
  private final @NotNull TextLocation location;

  protected AbstractTagProjectionEntry(@NotNull TagApi tag, @NotNull MP projection, @NotNull TextLocation location) {
    this.tag = tag;
    this.projection = projection;
    this.location = location;

    if (!tag.type().isAssignableFrom(projection.model()))
      throw new IllegalArgumentException(
          String.format(
              "Tag '%s' type '%s' is not compatible with '%s' projection",
              tag.name(), tag.type(), projection().model().name()
          )
      );

  }

  @Override
  public @NotNull TagApi tag() { return tag; }

  @Override
  public @NotNull MP projection() { return projection; }

  @SuppressWarnings("unchecked")
  @Override
  public @Nullable TP mergeTags(final @NotNull TagApi tag, final @NotNull List<TP> tagEntries) {
    if (tagEntries.isEmpty()) return null;

    final List<@NotNull MP> models =
        tagEntries.stream().map(AbstractTagProjectionEntry::projection).collect(Collectors.toList());

    final @NotNull MP mp = models.get(0);
    final @NotNull DatumTypeApi type = tag.type();
    MP mergedModel = ((GenModelProjection<MP, MP, DatumTypeApi>) mp).merge(type, models);

    return mergedModel == null ? null : mergeTags(tag, tagEntries, mergedModel);
  }

  protected @Nullable TP mergeTags(
      final @NotNull TagApi tag,
      final @NotNull List<TP> tagsEntries,
      @NotNull MP mergedModel) {

    throw new RuntimeException("Unsupported operation"); // todo remove this method from here
  }

  @Override
  public @NotNull TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractTagProjectionEntry<?, ?> that = (AbstractTagProjectionEntry<?, ?>) o;
    return Objects.equals(tag.name(), that.tag.name()) && Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(tag.name(), projection); }
}
