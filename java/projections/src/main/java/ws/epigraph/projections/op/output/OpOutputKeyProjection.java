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

package ws.epigraph.projections.op.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputKeyProjection {
  public enum Presence {OPTIONAL, REQUIRED, FORBIDDEN}

  @NotNull
  private final Presence presence;
  @NotNull
  private final OpParams params;
  @NotNull
  private final Annotations annotations;
  @NotNull
  private final TextLocation location;

  public OpOutputKeyProjection(@NotNull Presence presence,
                               @NotNull OpParams params,
                               @NotNull Annotations annotations,
                               @NotNull TextLocation location) {
    this.presence = presence;
    this.params = params;
    this.annotations = annotations;
    this.location = location;
  }

  @NotNull
  public Presence presence() { return presence; }

  @NotNull
  public OpParams params() { return params; }

  @NotNull
  public Annotations annotations() { return annotations; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpOutputKeyProjection that = (OpOutputKeyProjection) o;
    return presence == that.presence && Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(presence, annotations);
  }
}
