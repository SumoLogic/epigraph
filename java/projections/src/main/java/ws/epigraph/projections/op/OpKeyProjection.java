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

package ws.epigraph.projections.op;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.types.DatumTypeApi;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpKeyProjection {
  private final @NotNull OpParams params;
  private final @NotNull Annotations annotations;
  private final @Nullable OpInputModelProjection<?, ?, ?, ?> projection;
  private final @NotNull TextLocation location;

  public OpKeyProjection(
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> projection,
      @NotNull TextLocation location) {

    this.params = params;
    this.annotations = annotations;
    this.projection = projection;
    this.location = location;
  }

  public @NotNull OpParams params() { return params; }

  public @NotNull Annotations annotations() { return annotations; }

  public @Nullable OpInputModelProjection<?, ?, ?, ?> projection() { return projection; }

  public @NotNull TextLocation location() { return location; }

  public static @Nullable OpInputModelProjection<?, ?, ?, ?> mergeProjections(
      @NotNull DatumTypeApi model,
      @NotNull Stream<OpInputModelProjection<?, ?, ?, ?>> projectionStream) {

    List<OpInputModelProjection<?, ?, ?, ?>> projections =
        projectionStream.filter(Objects::nonNull).collect(Collectors.toList());

    if (projections.isEmpty()) return null;
    return merge(model, projections);
  }

  @SuppressWarnings("unchecked")
  private static <SMP extends OpInputModelProjection<?, SMP, M, ?>, M extends DatumTypeApi>
  @Nullable OpInputModelProjection<?, ?, ?, ?> merge(
      DatumTypeApi model,
      List<OpInputModelProjection<?, ?, ?, ?>> projections) {

    List<SMP> l = (List<SMP>) projections;
    return l.get(0).merge((M) model, l);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final OpKeyProjection that = (OpKeyProjection) o;
    return Objects.equals(params, that.params) &&
           Objects.equals(projection, that.projection) &&
           Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() { return Objects.hash(params, annotations, projection); }
}
