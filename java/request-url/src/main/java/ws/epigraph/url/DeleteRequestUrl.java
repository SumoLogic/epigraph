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

package ws.epigraph.url;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.projections.req.delete.ReqDeleteFieldProjection;

import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DeleteRequestUrl extends RequestUrl {
  @NotNull
  private final ReqDeleteFieldProjection deleteProjection;

  public DeleteRequestUrl(
      @NotNull final String fieldName,
      @Nullable final ReqFieldPath path,
      @NotNull ReqDeleteFieldProjection deleteProjection,
      @NotNull final StepsAndProjection<ReqOutputFieldProjection> outputProjection,
      @NotNull final Map<String, GDatum> parameters) {
    super(fieldName, path, outputProjection, parameters);

    this.deleteProjection = deleteProjection;
  }

  @NotNull
  public ReqDeleteFieldProjection deleteProjection() { return deleteProjection; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final DeleteRequestUrl url = (DeleteRequestUrl) o;
    return Objects.equals(deleteProjection, url.deleteProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deleteProjection);
  }
}
