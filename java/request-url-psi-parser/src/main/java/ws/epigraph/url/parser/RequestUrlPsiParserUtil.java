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

package ws.epigraph.url.parser;

import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.output.OpOutputFieldProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkFieldProjection;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionsPsiParser;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class RequestUrlPsiParserUtil {

  private RequestUrlPsiParserUtil() {}

  static @NotNull StepsAndProjection<ReqOutputFieldProjection> parseOutputProjection(
      final @NotNull DataTypeApi dataType,
      final @NotNull OpOutputFieldProjection op,
      final @Nullable UrlReqOutputTrunkFieldProjection psi,
      final @NotNull TypesResolver resolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection;

    if (psi == null) {
      stepsAndProjection = new StepsAndProjection<>(
          0,
          new ReqOutputFieldProjection(
              ReqParams.EMPTY,
              Annotations.EMPTY,
              ReqOutputProjectionsPsiParser.createDefaultVarProjection(
                  dataType,
                  op.varProjection(),
                  false,
                  PsiUtil.NULL_PSI_ELEMENT,
                  errors
              ),
              false,
              TextLocation.UNKNOWN
          )
      );
    } else {
      final StepsAndProjection<ReqOutputFieldProjection> fieldStepsAndProjection =
          ReqOutputProjectionsPsiParser.parseTrunkFieldProjection(
              true, dataType,
              op,
              psi,
              resolver,
              errors
          );

      int fieldPathSteps = fieldStepsAndProjection.pathSteps();

      stepsAndProjection = new StepsAndProjection<>(
          fieldPathSteps == 0 ? 0 : fieldPathSteps - 1,
          fieldStepsAndProjection.projection()
      );
    }

    return stepsAndProjection;
  }

}
