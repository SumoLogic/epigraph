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
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractTagProjectionEntry<MP extends AbstractModelProjection</*MP*/?, ?>>
    implements GenTagProjectionEntry<MP> {
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
  }

  @NotNull
  public Type.Tag tag() { return tag; }

  @NotNull
  public MP projection() { return projection; }

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
