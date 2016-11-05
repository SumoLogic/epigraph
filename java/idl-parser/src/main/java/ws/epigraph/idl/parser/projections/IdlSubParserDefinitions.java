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

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlSubParserDefinitions {
  public static final IdlSubParserDefinition OP_VAR_PATH = new IdlSubParserDefinition(I_OP_VAR_PATH);
  public static final IdlSubParserDefinition OP_OUTPUT_VAR_PROJECTION = new IdlSubParserDefinition(I_OP_OUTPUT_VAR_PROJECTION);
  public static final IdlSubParserDefinition OP_INPUT_VAR_PROJECTION = new IdlSubParserDefinition(I_OP_INPUT_VAR_PROJECTION);
  public static final IdlSubParserDefinition OP_DELETE_VAR_PROJECTION = new IdlSubParserDefinition(I_OP_DELETE_VAR_PROJECTION);

  @NotNull
  public static final IdlSubParserDefinitions.IdlSubParserDefinition DATA_VALUE =
      new IdlSubParserDefinition(I_DATA_VALUE);

  public static class IdlSubParserDefinition extends IdlParserDefinition {

    @NotNull
    private final IElementType rootElementType;

    private IdlSubParserDefinition(@NotNull IElementType rootElementType) {
      this.rootElementType = rootElementType;
    }

    @Override
    public PsiParser createParser(Project project) {
      return new IdlSubParser(rootElementType);
    }

    @NotNull
    public IElementType rootElementType() {
      return rootElementType;
    }
  }
}
