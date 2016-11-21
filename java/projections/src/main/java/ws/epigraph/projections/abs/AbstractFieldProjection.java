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
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenFieldProjection;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractFieldProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?>
    > implements GenFieldProjection<VP, TP, MP> {

  @NotNull
  private final Annotations annotations;

  @NotNull
  private final VP projection;

  @NotNull
  private final TextLocation location;

  protected AbstractFieldProjection(
      @NotNull Annotations annotations,
      @NotNull VP projection,
      @NotNull TextLocation location) {
    this.annotations = annotations;
    this.projection = projection;
    this.location = location;
  }

  @NotNull
  @Override
  public Annotations annotations() { return annotations; }

  @NotNull
  @Override
  public VP projection() { return projection; }

  @NotNull
  @Override
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractFieldProjection<?, ?, ?> that = (AbstractFieldProjection<?, ?, ?>) o;
    return Objects.equals(annotations, that.annotations) &&
           Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotations, projection);
  }
}
