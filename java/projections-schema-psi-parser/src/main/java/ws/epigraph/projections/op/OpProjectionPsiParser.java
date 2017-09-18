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

package ws.epigraph.projections.op;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.op.output.OpOutputFieldProjection;
import ws.epigraph.projections.op.output.OpOutputModelProjection;
import ws.epigraph.projections.op.output.OpOutputPsiProcessingContext;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.psi.SchemaOpOutputFieldProjection;
import ws.epigraph.schema.parser.psi.SchemaOpOutputModelProjection;
import ws.epigraph.schema.parser.psi.SchemaOpOutputUnnamedOrRefVarProjection;
import ws.epigraph.schema.parser.psi.SchemaOpOutputVarProjection;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.DatumTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface OpProjectionPsiParser {

  @NotNull OpOutputVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpOutputVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException;

  @NotNull OpOutputFieldProjection parseFieldProjection(
      @NotNull DataTypeApi fieldType,
      boolean flagged,
      @NotNull SchemaOpOutputFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException;

  @NotNull OpOutputVarProjection parseUnnamedOrRefVarProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpOutputUnnamedOrRefVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException;

  @NotNull OpOutputModelProjection<?, ?, ?, ?> parseModelProjection(
      @NotNull DatumTypeApi type,
      boolean flagged,
      @NotNull SchemaOpOutputModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException;
}
