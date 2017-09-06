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

package ws.epigraph.projections.op.output;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.psi.SchemaOpOutputFieldProjection;
import ws.epigraph.schema.parser.psi.SchemaOpOutputUnnamedOrRefVarProjection;
import ws.epigraph.schema.parser.psi.SchemaOpOutputVarProjection;
import ws.epigraph.types.DataTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("ConstantConditions")
public final class OpOutputProjectionsPsiParser {
  // so it's easy to toggle for debugging
  private static final boolean enabled = false;
//  private static final boolean enabled = true;

  private OpOutputProjectionsPsiParser() {}

  public static @NotNull OpOutputVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpOutputVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    OpOutputVarProjection res =
        OpProjectionsPsiParser.parseVarProjection(dataType, flagged, psi, typesResolver, context);

    return enabled
           ? new ParsedOutputProjectionPostProcessor(context).transform(res)
           : res;
  }

  public static @NotNull OpOutputFieldProjection parseFieldProjection(
      @NotNull DataTypeApi fieldType,
      boolean flagged,
      @NotNull SchemaOpOutputFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    OpOutputFieldProjection res =
        OpProjectionsPsiParser.parseFieldProjection(fieldType, flagged, psi, resolver, context);

    return enabled
           ? new ParsedOutputProjectionPostProcessor(context).transform(res)
           : res;
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

    return enabled
           ? new ParsedOutputProjectionPostProcessor(context).transform(res)
           : res;
  }
}
