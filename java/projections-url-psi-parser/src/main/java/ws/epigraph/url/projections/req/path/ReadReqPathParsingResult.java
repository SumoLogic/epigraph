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

package ws.epigraph.url.projections.req.path;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.url.parser.psi.UrlReqOutputComaVarProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadReqPathParsingResult<P> {
  private final @NotNull P path;

  // only one of these can be non-null
  private final @Nullable UrlReqOutputTrunkVarProjection trunkProjectionPsi;
  private final @Nullable UrlReqOutputComaVarProjection comaProjectionPsi;

  public ReadReqPathParsingResult(
      @NotNull P path,
      @Nullable UrlReqOutputTrunkVarProjection trunkProjectionPsi,
      @Nullable UrlReqOutputComaVarProjection comaProjectionPsi) {

    this.path = path;
    this.trunkProjectionPsi = trunkProjectionPsi;
    this.comaProjectionPsi = comaProjectionPsi;
  }

  public @NotNull P path() { return path; }

  public @Nullable UrlReqOutputTrunkVarProjection trunkProjectionPsi() { return trunkProjectionPsi; }

  public @Nullable UrlReqOutputComaVarProjection comaProjectionPsi() { return comaProjectionPsi; }
}
