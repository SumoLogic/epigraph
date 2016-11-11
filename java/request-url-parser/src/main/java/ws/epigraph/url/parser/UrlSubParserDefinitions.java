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

import com.intellij.lang.PsiParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.psi.SubParserDefinition;
import ws.epigraph.url.parser.psi.*;

import static ws.epigraph.url.lexer.UrlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlSubParserDefinitions {
  public static final UrlSubParserDefinition<UrlReadUrl> READ_URL =
      new UrlSubParserDefinition<>(U_READ_URL, UrlReadUrl.class);

  public static final UrlSubParserDefinition<UrlCreateUrl> CREATE_URL =
      new UrlSubParserDefinition<>(U_CREATE_URL, UrlCreateUrl.class);

  public static final UrlSubParserDefinition<UrlUpdateUrl> UPDATE_URL =
      new UrlSubParserDefinition<>(U_UPDATE_URL, UrlUpdateUrl.class);

  public static final UrlSubParserDefinition<UrlDeleteUrl> DELETE_URL =
      new UrlSubParserDefinition<>(U_DELETE_URL, UrlDeleteUrl.class);

  public static final UrlSubParserDefinition<UrlCustomUrl> CUSTOM_URL =
      new UrlSubParserDefinition<>(U_CUSTOM_URL, UrlCustomUrl.class);

  public static final UrlSubParserDefinition<UrlReqVarPath> REQ_VAR_PATH =
      new UrlSubParserDefinition<>(U_REQ_VAR_PATH, UrlReqVarPath.class);

  public static final UrlSubParserDefinition<UrlReqOutputTrunkVarProjection> REQ_OUTPUT_VAR_PROJECTION =
      new UrlSubParserDefinition<>(U_REQ_OUTPUT_TRUNK_VAR_PROJECTION, UrlReqOutputTrunkVarProjection.class);

  public static final UrlSubParserDefinition<UrlReqUpdateVarProjection> REQ_UPDATE_VAR_PROJECTION =
      new UrlSubParserDefinition<>(U_REQ_UPDATE_VAR_PROJECTION, UrlReqUpdateVarProjection.class);
  
  public static final UrlSubParserDefinition<UrlReqDeleteVarProjection> REQ_DELETE_VAR_PROJECTION =
      new UrlSubParserDefinition<>(U_REQ_DELETE_VAR_PROJECTION, UrlReqDeleteVarProjection.class);

  public static class UrlSubParserDefinition<T> extends UrlParserDefinition implements SubParserDefinition<T> {
    @NotNull
    private final Class<T> rootElementClass;
    @NotNull
    private final IElementType rootElementType;

    public UrlSubParserDefinition(@NotNull IElementType rootElementType, final @NotNull Class<T> rootElementClass) {
      this.rootElementType = rootElementType;
      this.rootElementClass = rootElementClass;
    }

    @Override
    public PsiParser createParser(Project project) {
      return new UrlSubParser(rootElementType);
    }

    @NotNull
    public IElementType rootElementType() {
      return rootElementType;
    }

    @NotNull
    @Override
    public Class<T> rootElementClass() {
      return rootElementClass;
    }
  }
}
