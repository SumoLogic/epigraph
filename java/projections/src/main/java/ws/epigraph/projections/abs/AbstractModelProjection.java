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
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractModelProjection<MP extends GenModelProjection</*MP*/?, ?>, M extends DatumType>
    implements GenModelProjection<MP, M> {
  @NotNull
  protected final M model;
  @Nullable
  protected final MP metaProjection;
  @NotNull
  protected final Annotations annotations;
  @NotNull
  private final TextLocation location;

  public AbstractModelProjection(
      @NotNull M model,
      @Nullable MP metaProjection,
      @NotNull Annotations annotations,
      @NotNull TextLocation location) {
    this.model = model;
    this.metaProjection = metaProjection;
    this.annotations = annotations;
    this.location = location;
  }

  @NotNull
  public M model() { return model; }

  @Nullable
  @Override
  public MP metaProjection() { return metaProjection; }

  @NotNull
  public Annotations annotations() { return annotations; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractModelProjection<?, ?> that = (AbstractModelProjection<?, ?>) o;
    return Objects.equals(model, that.model) &&
           Objects.equals(metaProjection, that.metaProjection) &&
           Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(model, metaProjection, annotations);
  }
}
