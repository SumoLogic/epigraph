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
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.*;
import ws.epigraph.types.FieldApi;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractFieldProjectionEntry<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, /*SMP*/?, ?, ?>,
    FP extends GenFieldProjection<VP, TP, MP, FP>
    > implements GenFieldProjectionEntry<VP, TP, MP, FP> {

  private final @NotNull FieldApi field;
  private final @NotNull FP projection;
  private final @NotNull TextLocation location;

  protected AbstractFieldProjectionEntry(
      @NotNull FieldApi field,
      @NotNull FP projection,
      @NotNull TextLocation location
  ) {
    this.field = field;
    this.projection = projection;
    this.location = location;
  }

  @Override
  public @NotNull FieldApi field() { return field; }

  @Override
  public @NotNull FP fieldProjection() { return projection; }

  @Override
  public @NotNull TextLocation location() { return location; }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractFieldProjectionEntry<VP, TP, MP, FP> that = (AbstractFieldProjectionEntry<VP, TP, MP, FP>) o;
    return Objects.equals(field.name(), that.field.name()) &&
           Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(field.name(), projection);
  }
}
