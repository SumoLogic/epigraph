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

package ws.epigraph.url.projections.req;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.url.parser.psi.UrlReqComaEntityProjection;
import ws.epigraph.url.parser.psi.UrlReqTrunkEntityProjection;
import ws.epigraph.url.parser.psi.UrlReqTrunkFieldProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface ReqProjectionPsiParser {
  @NotNull StepsAndProjection<ReqEntityProjection> parseTrunkEntityProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpEntityProjection op,
      @NotNull UrlReqTrunkEntityProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException;

  @NotNull StepsAndProjection<ReqEntityProjection> parseComaEntityProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpEntityProjection op,
      @NotNull UrlReqComaEntityProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException;

  ReqEntityProjection createDefaultEntityProjection(
      @NotNull DataTypeApi type,
      @NotNull OpEntityProjection op,
      boolean required,
      @NotNull  TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException;

  @NotNull StepsAndProjection<ReqFieldProjection> parseTrunkFieldProjection(
      @NotNull DataTypeApi fieldType,
      boolean flagged,
      @NotNull OpFieldProjection op,
      @NotNull UrlReqTrunkFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context) throws PsiProcessingException;
}
