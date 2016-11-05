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

package ws.epigraph.projections.req.delete;

import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteKeyProjection {
  @NotNull
  private final Datum value;
  @NotNull
  private final ReqParams params;
  @NotNull
  private final Annotations annotations;
  @NotNull
  private final TextLocation location;

  public ReqDeleteKeyProjection(@NotNull Datum value,
                                @NotNull ReqParams params,
                                @NotNull Annotations annotations,
                                @NotNull TextLocation location) {
    this.value = value;
    this.params = params;
    this.annotations = annotations;
    this.location = location;
  }

  @NotNull
  public Datum value() { return value; }

  @NotNull
  public ReqParams params() { return params; }

  @NotNull
  public Annotations annotations() { return annotations; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReqDeleteKeyProjection that = (ReqDeleteKeyProjection) o;
    return Objects.equals(value, that.value) &&
           Objects.equals(params, that.params) &&
           Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() { return Objects.hash(value, params, annotations); }
}
