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

package ws.epigraph.projections.req.path;

import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.req.Directives;
import ws.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPathKeyProjection {
  private final @NotNull Datum value;
  private final @NotNull ReqParams params;
  private final @NotNull Directives directives;
  private final @NotNull TextLocation location;

  public ReqPathKeyProjection(
      @NotNull Datum value,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull TextLocation location) {
    this.value = value;
    this.params = params;
    this.directives = directives;
    this.location = location;
  }

  public @NotNull Datum value() { return value; }

  public @NotNull ReqParams params() { return params; }

  public @NotNull Directives annotations() { return directives; }

  public @NotNull TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReqPathKeyProjection that = (ReqPathKeyProjection) o;
    return Objects.equals(value, that.value) &&
           Objects.equals(params, that.params) &&
           Objects.equals(directives, that.directives);
  }

  @Override
  public int hashCode() { return Objects.hash(value, params, directives); }
}
