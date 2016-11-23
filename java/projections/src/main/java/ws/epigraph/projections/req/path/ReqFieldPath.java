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

package ws.epigraph.projections.req.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractFieldProjection;
import ws.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqFieldPath extends AbstractFieldProjection<
    ReqVarPath,
    ReqTagPath,
    ReqModelPath<?, ?>,
    ReqFieldPath
    > {
  @NotNull
  private final ReqParams reqParams;

  public ReqFieldPath(
      @NotNull ReqParams reqParams,
      @NotNull Annotations annotations,
      @NotNull ReqVarPath projection,
      @NotNull TextLocation location) {
    super(annotations, projection, location);
    this.reqParams = reqParams;
  }

  @NotNull
  public ReqParams reqParams() { return reqParams; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqFieldPath that = (ReqFieldPath) o;
    return Objects.equals(reqParams, that.reqParams);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), reqParams);
  }
}
