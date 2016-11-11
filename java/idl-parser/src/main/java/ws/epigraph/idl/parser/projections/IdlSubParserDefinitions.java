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

package ws.epigraph.idl.parser.projections;

import com.intellij.lang.PsiParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import ws.epigraph.idl.parser.IdlParserDefinition;
import ws.epigraph.idl.parser.IdlSubParser;

import static ws.epigraph.idl.lexer.IdlElementTypes.*;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.idl.parser.psi.*;
import ws.epigraph.psi.SubParserDefinition;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlSubParserDefinitions {
  public static final IdlSubParserDefinition<IdlOpVarPath> OP_VAR_PATH =
      new IdlSubParserDefinition<>(I_OP_VAR_PATH, IdlOpVarPath.class);

  public static final IdlSubParserDefinition<IdlOpOutputVarProjection> OP_OUTPUT_VAR_PROJECTION =
      new IdlSubParserDefinition<>(I_OP_OUTPUT_VAR_PROJECTION, IdlOpOutputVarProjection.class);

  public static final IdlSubParserDefinition<IdlOpInputVarProjection> OP_INPUT_VAR_PROJECTION =
      new IdlSubParserDefinition<>(I_OP_INPUT_VAR_PROJECTION, IdlOpInputVarProjection.class);

  public static final IdlSubParserDefinition<IdlOpDeleteVarProjection> OP_DELETE_VAR_PROJECTION =
      new IdlSubParserDefinition<>(I_OP_DELETE_VAR_PROJECTION, IdlOpDeleteVarProjection.class);

  public static final IdlSubParserDefinition<IdlDataValue> DATA_VALUE =
      new IdlSubParserDefinition<>(I_DATA_VALUE, IdlDataValue.class);

  public static class IdlSubParserDefinition<T> extends IdlParserDefinition implements SubParserDefinition<T> {
    @NotNull
    private final Class<T> rootElementClass;

    @NotNull
    private final IElementType rootElementType;

    private IdlSubParserDefinition(@NotNull IElementType rootElementType, final @NotNull Class<T> rootElementClass) {
      this.rootElementType = rootElementType;
      this.rootElementClass = rootElementClass;
    }

    @Override
    public PsiParser createParser(Project project) { return new IdlSubParser(rootElementType); }

    @NotNull
    public IElementType rootElementType() { return rootElementType; }

    @NotNull
    @Override
    public Class<T> rootElementClass() { return rootElementClass; }
  }
}
