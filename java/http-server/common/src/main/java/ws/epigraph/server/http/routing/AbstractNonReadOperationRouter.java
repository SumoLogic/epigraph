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

package ws.epigraph.server.http.routing;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.service.operations.Operation;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.url.RequestUrl;
import ws.epigraph.url.parser.RequestUrlPsiParser;
import ws.epigraph.url.parser.psi.UrlUrl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractNonReadOperationRouter<OD extends OperationDeclaration, O extends Operation<OD, ?, ?>>
    extends AbstractOperationRouter<UrlUrl, OD, O> {

  private final @NotNull RequestUrlPsiParser requestUrlPsiParser;

  protected AbstractNonReadOperationRouter(final @NotNull RequestUrlPsiParser requestUrlPsiParser) {
    this.requestUrlPsiParser = requestUrlPsiParser;
  }

  @Override
  protected @NotNull RequestUrl parseUrl(
      final @NotNull DataTypeApi resourceType,
      final @NotNull OD opDecl,
      final @NotNull UrlUrl urlPsi,
      final @NotNull TypesResolver resolver,
      final @NotNull PsiProcessingContext context) throws PsiProcessingException {

    return requestUrlPsiParser.parseRequestUrl(
        resourceType,
        opDecl,
        urlPsi,
        resolver,
        context
    );
  }
}
