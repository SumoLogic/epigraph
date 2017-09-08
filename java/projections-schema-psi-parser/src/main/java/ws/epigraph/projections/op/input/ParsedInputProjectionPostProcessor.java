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

package ws.epigraph.projections.op.input;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.op.OpProjectionTransformer;
import ws.epigraph.psi.PsiProcessingContext;

/**
 * Post-processor for input projections. Does a few things:
 * <ul>
 *   <li>Checks that required (+) entity projections are either self-vars or have a retro tag</li>
 *   <li>If the above test passes: marks all entity models as required</li>
 * </ul>
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ParsedInputProjectionPostProcessor extends OpProjectionTransformer {
  private final @NotNull PsiProcessingContext context;

  ParsedInputProjectionPostProcessor(final @NotNull PsiProcessingContext context) {this.context = context;}


  // todo
  /*
  public static @NotNull OpOutputVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpOutputVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    OpOutputVarProjection res =
        OpProjectionsPsiParser.parseVarProjection(dataType, flagged, psi, typesResolver, context);

    if (enabled) {
      OpProjectionTransformationMap transformationMap = new OpProjectionTransformationMap();
      OpOutputVarProjection transformed =
          new ParsedOutputProjectionPostProcessor(context).transform(transformationMap, res);
      context.referenceContext().transform(transformationMap);
      return transformed;
    } else
      return res;
  }

  public static @NotNull OpOutputFieldProjection parseFieldProjection(
      @NotNull DataTypeApi fieldType,
      boolean flagged,
      @NotNull SchemaOpOutputFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    OpOutputFieldProjection res =
        OpProjectionsPsiParser.parseFieldProjection(fieldType, flagged, psi, resolver, context);

    if (enabled) {
      OpProjectionTransformationMap transformationMap = new OpProjectionTransformationMap();
      @NotNull OpOutputFieldProjection transformed =
          new ParsedOutputProjectionPostProcessor(context).transform(transformationMap, res);
      context.referenceContext().transform(transformationMap);
      return transformed;
    } else
      return res;
  }

  public static OpOutputVarProjection parseUnnamedOrRefVarProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpOutputUnnamedOrRefVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    OpOutputVarProjection res =
        OpProjectionsPsiParser.parseUnnamedOrRefVarProjection(dataType, flagged, psi, typesResolver, context);

    if (enabled) {
      OpProjectionTransformationMap transformationMap = new OpProjectionTransformationMap();
      OpOutputVarProjection transformed =
          new ParsedOutputProjectionPostProcessor(context).transform(transformationMap, res);
      context.referenceContext().transform(transformationMap);
      return transformed;
    } else
      return res;
  }
  */

}
