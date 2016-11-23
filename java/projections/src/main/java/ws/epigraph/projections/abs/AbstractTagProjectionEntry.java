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
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.types.Type;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractTagProjectionEntry<
    TP extends AbstractTagProjectionEntry<TP, MP>,
    MP extends AbstractModelProjection</*MP*/?, ?>> implements GenTagProjectionEntry<TP, MP> {

  @NotNull
  private final Type.Tag tag;
  @NotNull
  private final MP projection;
  @NotNull
  private final TextLocation location;

  public AbstractTagProjectionEntry(@NotNull Type.Tag tag, @NotNull MP projection, @NotNull TextLocation location) {
    this.tag = tag;
    this.projection = projection;
    this.location = location;

    if (!tag.type.isAssignableFrom(projection.model()))
      throw new IllegalArgumentException(
          String.format(
              "Tag '%s' type '%s' is not compatible with '%s' projection",
              tag.name(), tag.type, projection().model().name()
          )
      );

  }

  @NotNull
  public Type.Tag tag() { return tag; }

  @NotNull
  public MP projection() { return projection; }

  @Nullable
  @Override
  @SuppressWarnings("unchecked")
  public TP mergeTags(@NotNull final Type.Tag tag, @NotNull final List<TP> tagEntries) {
    if (tagEntries.isEmpty()) return null;

    final List<@NotNull MP> models =
        tagEntries.stream().map(AbstractTagProjectionEntry::projection).collect(Collectors.toList());

    MP mergedModel = (MP) models.get(0).merge(tag.type, models);

    return mergedModel == null ? null : mergeTags(tag, tagEntries, mergedModel);
  }

  @Nullable
  protected TP mergeTags(@NotNull final Type.Tag tag, @NotNull final List<TP> tagsEntries, @NotNull MP mergedModel) {
    throw new RuntimeException("Unsupported operation"); // todo remove this method from here
  }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractTagProjectionEntry that = (AbstractTagProjectionEntry) o;
    return Objects.equals(tag.name(), that.tag.name()) && Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(tag.name(), projection); }
}
