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

package ws.epigraph.url.projections.req.path;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.url.parser.psi.UrlReqComaEntityProjection;
import ws.epigraph.url.parser.psi.UrlReqTrunkEntityProjection;

/**
 * Result of partial path parsing. Contains parsed path of type {@code P}
 * and optional unparsed tail, which is either a trunk or a coma projection psi.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPartialPathParsingResult<P> {
  private final @NotNull P path;

  // only one of these can be non-null
  private final @Nullable UrlReqTrunkEntityProjection trunkProjectionPsi;
  private final @Nullable UrlReqComaEntityProjection comaProjectionPsi;

  public ReqPartialPathParsingResult(
      @NotNull P path,
      @Nullable UrlReqTrunkEntityProjection trunkProjectionPsi,
      @Nullable UrlReqComaEntityProjection comaProjectionPsi) {

    this.path = path;
    this.trunkProjectionPsi = trunkProjectionPsi;
    this.comaProjectionPsi = comaProjectionPsi;
  }

  public @NotNull P path() { return path; }

  public @Nullable UrlReqTrunkEntityProjection trunkProjectionPsi() { return trunkProjectionPsi; }

  public @Nullable UrlReqComaEntityProjection comaProjectionPsi() { return comaProjectionPsi; }
}
