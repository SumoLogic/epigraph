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
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.psi.SchemaOpEntityProjection;
import ws.epigraph.schema.parser.psi.SchemaOpFieldProjection;
import ws.epigraph.schema.parser.psi.SchemaOpModelProjection;
import ws.epigraph.schema.parser.psi.SchemaOpUnnamedOrRefEntityProjection;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.DatumTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface OpProjectionPsiParser {

  @NotNull OpProjection<?, ?> parseProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpEntityProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException;

  @NotNull OpFieldProjection parseFieldProjection(
      @NotNull DataTypeApi fieldType,
      boolean flagged,
      @NotNull SchemaOpFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException;

  @NotNull OpProjection<?, ?> parseUnnamedOrRefProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpUnnamedOrRefEntityProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException;

  @NotNull OpModelProjection<?, ?, ?, ?> parseModelProjection(
      @NotNull DatumTypeApi type,
      boolean flagged,
      @NotNull SchemaOpModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context)
      throws PsiProcessingException;
}
