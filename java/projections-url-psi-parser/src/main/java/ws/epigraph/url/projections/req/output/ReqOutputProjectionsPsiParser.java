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

package ws.epigraph.url.projections.req.output;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.output.OpOutputFieldProjection;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.url.parser.psi.UrlReqOutputComaVarProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkFieldProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputProjectionsPsiParser {
  public static @NotNull StepsAndProjection<ReqOutputVarProjection> parseTrunkVarProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpOutputVarProjection op,
      @NotNull UrlReqOutputTrunkVarProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqOutputPsiProcessingContext context) throws PsiProcessingException {

    StepsAndProjection<ReqOutputVarProjection> stepsAndProjection = ReqProjectionsPsiParser.parseTrunkVarProjection(
        dataType, flagged, op, psi, resolver, context
    );

    return stepsAndProjection;
  }

  public static @NotNull StepsAndProjection<ReqOutputVarProjection> parseComaVarProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpOutputVarProjection op,
      @NotNull UrlReqOutputComaVarProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqOutputPsiProcessingContext context) throws PsiProcessingException {

    StepsAndProjection<ReqOutputVarProjection> stepsAndProjection = ReqProjectionsPsiParser.parseComaVarProjection(
        dataType, flagged, op, psi, resolver, context
    );

    return stepsAndProjection;
  }

  public static @NotNull ReqOutputVarProjection createDefaultVarProjection(
      @NotNull DataTypeApi type,
      @NotNull OpOutputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull ReqOutputPsiProcessingContext context) throws PsiProcessingException {

    ReqOutputVarProjection ep = ReqProjectionsPsiParser.createDefaultVarProjection(
        type, op, required, locationPsi, context
    );

    return ep;
  }

  public static @NotNull StepsAndProjection<ReqOutputFieldProjection> parseTrunkFieldProjection(
      @NotNull DataTypeApi fieldType,
      boolean flagged,
      @NotNull OpOutputFieldProjection op,
      @NotNull UrlReqOutputTrunkFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqOutputPsiProcessingContext context) throws PsiProcessingException {

    StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection = ReqProjectionsPsiParser.parseTrunkFieldProjection(
        fieldType, flagged, op, psi, resolver, context
    );

    return stepsAndProjection;
  }
}
