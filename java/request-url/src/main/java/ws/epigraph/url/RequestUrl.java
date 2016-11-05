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

import ws.epigraph.gdata.GDatum;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class RequestUrl {
  @NotNull
  private final String fieldName;
  @Nullable
  private final ReqFieldPath path;
  @NotNull
  private final StepsAndProjection<ReqOutputFieldProjection> outputProjection;
  @NotNull
  private final Map<String, GDatum> parameters;

  public RequestUrl(
      @NotNull String fieldName,
      @Nullable ReqFieldPath path,
      @NotNull StepsAndProjection<ReqOutputFieldProjection> outputProjection,
      @NotNull Map<String, GDatum> parameters) {

    this.fieldName = fieldName;
    this.path = path;
    this.outputProjection = outputProjection;
    this.parameters = parameters;
  }

  @NotNull
  public String fieldName() { return fieldName; }

  public @Nullable ReqFieldPath path() { return path; }

  @NotNull
  public StepsAndProjection<ReqOutputFieldProjection> outputProjection() { return outputProjection; }

  @NotNull
  public Map<String, GDatum> parameters() { return parameters; }
}
