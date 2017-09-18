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
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CreateRequestUrl extends RequestUrl {
  private final @Nullable StepsAndProjection<ReqFieldProjection> inputProjection;

  public CreateRequestUrl(
      final @NotNull String fieldName,
      final @Nullable ReqFieldPath path,
      final @Nullable StepsAndProjection<ReqFieldProjection> inputProjection,
      final @NotNull StepsAndProjection<ReqFieldProjection> outputProjection,
      final @NotNull Map<String, GDatum> parameters) {
    super(fieldName, path, outputProjection, parameters);
    this.inputProjection = inputProjection;
  }

  public @Nullable StepsAndProjection<ReqFieldProjection> inputProjection() { return inputProjection; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final CreateRequestUrl url = (CreateRequestUrl) o;
    return Objects.equals(inputProjection, url.inputProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), inputProjection);
  }
}
