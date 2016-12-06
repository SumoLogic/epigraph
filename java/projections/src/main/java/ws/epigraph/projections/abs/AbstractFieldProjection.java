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
import ws.epigraph.types.DataType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractFieldProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?>,
    FP extends GenFieldProjection<VP, TP, MP, FP>
    > implements GenFieldProjection<VP, TP, MP, FP> {

  private final @NotNull Annotations annotations;

  private final @NotNull VP projection;

  private final @NotNull TextLocation location;

  protected AbstractFieldProjection(
      @NotNull Annotations annotations,
      @NotNull VP projection,
      @NotNull TextLocation location) {
    this.annotations = annotations;
    this.projection = projection;
    this.location = location;
  }

  @Override
  public @NotNull Annotations annotations() { return annotations; }

  @Override
  public @NotNull VP varProjection() { return projection; }

  @Override
  public @NotNull FP merge(final @NotNull DataType type, final @NotNull List<FP> fieldProjections) {
    if (fieldProjections.isEmpty()) throw new IllegalArgumentException("Can't merge empty list");
    if (fieldProjections.size() == 1) return fieldProjections.get(0);

    final List<@NotNull VP> varProjections =
        fieldProjections.stream().map(GenFieldProjection::varProjection).collect(Collectors.toList());

    assert varProjections.size() >= 1;

    final @NotNull VP mergedVarProjection = varProjections.get(0).merge(varProjections);

    return merge(
        type,
        fieldProjections,
        Annotations.merge(fieldProjections.stream().map(GenFieldProjection::annotations)),
        mergedVarProjection
    );
  }

  protected FP merge(
      @NotNull DataType type,
      @NotNull List<FP> fieldProjections,
      @NotNull Annotations mergedAnnotations,
      @NotNull VP mergedVarProjection) {

    throw new RuntimeException("not implemented"); // todo make abstract
  }

  @Override
  public @NotNull TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractFieldProjection<?, ?, ?, ?> that = (AbstractFieldProjection<?, ?, ?, ?>) o;
    return Objects.equals(annotations, that.annotations) &&
           Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotations, projection);
  }
}
