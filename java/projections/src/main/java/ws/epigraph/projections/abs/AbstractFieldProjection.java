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

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.*;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TypeApi;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractFieldProjection<
    P extends GenProjection</*P*/?, TP, ?, ?>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection<?, TP, /*MP*/?, ?, ?>,
    FP extends GenFieldProjection<P, TP, MP, FP>
    > implements GenFieldProjection<P, TP, MP, FP> {

//  private final @NotNull Annotations annotations;

  private final @NotNull P projection;

  private final @NotNull TextLocation location;

  protected AbstractFieldProjection(
//      @NotNull Annotations annotations,
      @NotNull P projection,
      @NotNull TextLocation location) {
//    this.annotations = annotations;
    this.projection = projection;
    this.location = location;
  }

//  @Override
//  public @NotNull Annotations annotations() { return annotations; }

  @Override
  public @NotNull P projection() { return projection; }

  @Override
  public FP merge(
      final @NotNull DataTypeApi type,
      final @NotNull List<FP> fieldProjections) {
    if (fieldProjections.isEmpty()) throw new IllegalArgumentException("Can't merge empty list");
    if (fieldProjections.size() == 1) return fieldProjections.get(0);

    final TypeApi targetType = type.type();
    @SuppressWarnings("unchecked")
    final List<@NotNull P> projections =
        fieldProjections
            .stream()
            .map(fp -> (P) fp.projection().normalizedForType(targetType))
            .collect(Collectors.toList());

    assert projections.size() >= 1;

    @SuppressWarnings("unchecked")
    final @NotNull P mergedProjection = (P) ProjectionUtils.merge(targetType, projections);

    return merge(
        type,
        fieldProjections,
        mergedProjection
    );
  }

  protected abstract FP merge(
      @NotNull DataTypeApi type,
      @NotNull List<FP> fieldProjections,
//      @NotNull Annotations mergedAnnotations,
      @NotNull P mergedEntityProjection);

  @Override
  public @NotNull TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractFieldProjection<?, ?, ?, ?> that = (AbstractFieldProjection<?, ?, ?, ?>) o;
    return /* Objects.equals(annotations, that.annotations) && */
        Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(/* annotations, */ projection);
  }
}
