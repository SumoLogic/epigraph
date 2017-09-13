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

package ws.epigraph.url;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;

import java.util.Map;
import java.util.Objects;

/**
 * Fully parsed request URL
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class RequestUrl {
  private final @NotNull String fieldName;
  private final @Nullable ReqFieldPath path;
  private final @NotNull StepsAndProjection<ReqFieldProjection> outputProjection;
  private final @NotNull Map<String, GDatum> parameters;

  protected RequestUrl(
      @NotNull String fieldName,
      @Nullable ReqFieldPath path,
      @NotNull StepsAndProjection<ReqFieldProjection> outputProjection,
      @NotNull Map<String, GDatum> parameters) {

    this.fieldName = fieldName;
    this.path = path;
    this.outputProjection = outputProjection;
    this.parameters = parameters;
  }

  public @NotNull String fieldName() { return fieldName; }

  public @Nullable ReqFieldPath path() { return path; }

  public @NotNull StepsAndProjection<ReqFieldProjection> outputProjection() { return outputProjection; }

  public @NotNull Map<String, GDatum> parameters() { return parameters; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final RequestUrl url = (RequestUrl) o;
    return Objects.equals(fieldName, url.fieldName) &&
           Objects.equals(path, url.path) &&
           Objects.equals(outputProjection, url.outputProjection) &&
           Objects.equals(parameters, url.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldName, path, outputProjection, parameters);
  }
}
