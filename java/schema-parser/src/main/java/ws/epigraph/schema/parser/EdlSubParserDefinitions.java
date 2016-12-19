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

package ws.epigraph.schema.parser;

import com.intellij.lang.PsiParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.psi.SubParserDefinition;
import ws.epigraph.schema.parser.psi.*;

import static ws.epigraph.schema.lexer.EdlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class EdlSubParserDefinitions {

  public static final EdlSubParserDefinition<EdlOpVarPath> OP_VAR_PATH =
      new EdlSubParserDefinition<>(S_OP_VAR_PATH, EdlOpVarPath.class);

  public static final EdlSubParserDefinition<EdlOpOutputVarProjection> OP_OUTPUT_VAR_PROJECTION =
      new EdlSubParserDefinition<>(S_OP_OUTPUT_VAR_PROJECTION, EdlOpOutputVarProjection.class);

  public static final EdlSubParserDefinition<EdlOpInputVarProjection> OP_INPUT_VAR_PROJECTION =
      new EdlSubParserDefinition<>(S_OP_INPUT_VAR_PROJECTION, EdlOpInputVarProjection.class);

  public static final EdlSubParserDefinition<EdlOpDeleteVarProjection> OP_DELETE_VAR_PROJECTION =
      new EdlSubParserDefinition<>(S_OP_DELETE_VAR_PROJECTION, EdlOpDeleteVarProjection.class);

  public static final EdlSubParserDefinition<EdlDataValue> DATA_VALUE =
      new EdlSubParserDefinition<>(S_DATA_VALUE, EdlDataValue.class);

  private EdlSubParserDefinitions() {}

  public static final class EdlSubParserDefinition<T> extends EdlParserDefinition implements SubParserDefinition<T> {
    private final @NotNull Class<T> rootElementClass;

    private final @NotNull IElementType rootElementType;

    private EdlSubParserDefinition(@NotNull IElementType rootElementType, final @NotNull Class<T> rootElementClass) {
      this.rootElementType = rootElementType;
      this.rootElementClass = rootElementClass;
    }

    @Override
    public PsiParser createParser(Project project) { return new EdlSubParser(rootElementType); }

    @Override
    public @NotNull IElementType rootElementType() { return rootElementType; }

    @Override
    public @NotNull Class<T> rootElementClass() { return rootElementClass; }
  }
}
