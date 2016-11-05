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

package ws.epigraph.url;

import ws.epigraph.url.parser.psi.*;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TypeRefs { // todo create and move to url-psi-parser-common?
  @NotNull
  public static TypeRef fromPsi(@NotNull UrlTypeRef psi) throws PsiProcessingException {
    if (psi instanceof UrlQnTypeRef) {
      UrlQnTypeRef fqnTypeRefPsi = (UrlQnTypeRef) psi;
      return new QnTypeRef(fqnTypeRefPsi.getQn().getQn());
    }

    if (psi instanceof UrlAnonList) {
      UrlAnonList anonListPsi = (UrlAnonList) psi;
      @Nullable UrlValueTypeRef valueTypeRefPsi = anonListPsi.getValueTypeRef();
      if (valueTypeRefPsi == null) throw new PsiProcessingException("List item type not specified", psi);
      return new AnonListRef(fromPsi(valueTypeRefPsi));
    }

    if (psi instanceof UrlAnonMap) {
      UrlAnonMap anonMapPsi = (UrlAnonMap) psi;

      @Nullable UrlTypeRef keyTypeRefPsi = anonMapPsi.getTypeRef();
      if (keyTypeRefPsi == null) throw new PsiProcessingException("Map key type not specified", psi);
      TypeRef keyTypeRef = fromPsi(keyTypeRefPsi);

      @Nullable UrlValueTypeRef valueTypeRefPsi = anonMapPsi.getValueTypeRef();
      if (valueTypeRefPsi == null) throw new PsiProcessingException("Map value type not specified", psi);
      ValueTypeRef valueTypeRef = fromPsi(valueTypeRefPsi);

      return new AnonMapRef(keyTypeRef, valueTypeRef);
    }

    throw new PsiProcessingException("Unknown reference type: " + psi.getClass().getName(), psi);
  }

  @NotNull
  public static ValueTypeRef fromPsi(@NotNull UrlValueTypeRef psi) throws PsiProcessingException {
    @Nullable UrlDefaultOverride defaultOverridePsi = psi.getDefaultOverride();

    @Nullable final String overrideTagName;
    if (defaultOverridePsi == null) overrideTagName = null;
    else {
      @Nullable final UrlVarTagRef varTagRef = defaultOverridePsi.getVarTagRef();
      overrideTagName = varTagRef == null ? null : varTagRef.getQid().getCanonicalName();
    }

    return new ValueTypeRef(fromPsi(psi.getTypeRef()), overrideTagName);
  }
}
