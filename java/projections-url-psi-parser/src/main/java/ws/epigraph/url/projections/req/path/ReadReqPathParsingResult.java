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

import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.url.parser.psi.UrlReqOutputComaVarProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadReqPathParsingResult<P> {
  @NotNull
  private final P path;

  // only one of these can be non-null
  @Nullable
  private final UrlReqOutputTrunkVarProjection trunkProjectionPsi;
  @Nullable
  private final UrlReqOutputComaVarProjection comaProjectionPsi;
  @NotNull
  private final List<PsiProcessingError> errors;

  public ReadReqPathParsingResult(
      @NotNull P path,
      @Nullable UrlReqOutputTrunkVarProjection trunkProjectionPsi,
      @Nullable UrlReqOutputComaVarProjection comaProjectionPsi,
      final @NotNull List<PsiProcessingError> errors) {

    this.path = path;
    this.trunkProjectionPsi = trunkProjectionPsi;
    this.comaProjectionPsi = comaProjectionPsi;
    this.errors = errors;
  }

  @NotNull
  public P path() { return path; }

  @Nullable
  public UrlReqOutputTrunkVarProjection trunkProjectionPsi() { return trunkProjectionPsi; }

  @Nullable
  public UrlReqOutputComaVarProjection comaProjectionPsi() { return comaProjectionPsi; }

  @NotNull
  public List<PsiProcessingError> errors() { return errors; }
}
