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
public final class UrlSubParserDefinitions {

  public static final UrlSubParserDefinition<UrlReadUrl> READ_URL =
      new UrlSubParserDefinition<>(U_READ_URL, UrlReadUrl.class);

  public static final UrlSubParserDefinition<UrlNonReadUrl> NON_READ_URL =
      new UrlSubParserDefinition<>(U_NON_READ_URL, UrlNonReadUrl.class);

  public static final UrlSubParserDefinition<UrlReqEntityPath> REQ_ENTITY_PATH =
      new UrlSubParserDefinition<>(U_REQ_ENTITY_PATH, UrlReqEntityPath.class);

  public static final UrlSubParserDefinition<UrlReqTrunkEntityProjection> REQ_ENTITY_PROJECTION =
      new UrlSubParserDefinition<>(U_REQ_TRUNK_ENTITY_PROJECTION, UrlReqTrunkEntityProjection.class);

  public static final UrlSubParserDefinition<UrlReqTrunkFieldProjection> REQ_FIELD_PROJECTION =
      new UrlSubParserDefinition<>(U_REQ_TRUNK_FIELD_PROJECTION, UrlReqTrunkFieldProjection.class);

  private UrlSubParserDefinitions() {}

  public static class UrlSubParserDefinition<T> extends UrlParserDefinition implements SubParserDefinition<T> {
    private final @NotNull Class<T> rootElementClass;
    private final @NotNull IElementType rootElementType;

    public UrlSubParserDefinition(@NotNull IElementType rootElementType, final @NotNull Class<T> rootElementClass) {
      this.rootElementType = rootElementType;
      this.rootElementClass = rootElementClass;
    }

    @Override
    public PsiParser createParser(Project project) {
      return new UrlSubParser(rootElementType);
    }

    @Override
    public @NotNull IElementType rootElementType() {
      return rootElementType;
    }

    @Override
    public @NotNull Class<T> rootElementClass() {
      return rootElementClass;
    }
  }
}
