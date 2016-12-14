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

package ws.epigraph.edl;

import ws.epigraph.edl.parser.psi.*;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class TypeRefs {
  private TypeRefs() {}

  public static @NotNull TypeRef fromPsi(@NotNull SchemaTypeRef psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    if (psi instanceof SchemaQnTypeRef) {
      SchemaQnTypeRef fqnTypeRefPsi = (SchemaQnTypeRef) psi;
      return new QnTypeRef(fqnTypeRefPsi.getQn().getQn());
    }

    if (psi instanceof SchemaAnonList) {
      SchemaAnonList anonListPsi = (SchemaAnonList) psi;
      @Nullable SchemaValueTypeRef valueTypeRefPsi = anonListPsi.getValueTypeRef();
      if (valueTypeRefPsi == null) throw new PsiProcessingException("List item type not specified", psi, errors);
      return new AnonListRef(fromPsi(valueTypeRefPsi, errors));
    }

    if (psi instanceof SchemaAnonMap) {
      SchemaAnonMap anonMapPsi = (SchemaAnonMap) psi;

      @Nullable SchemaTypeRef keyTypeRefPsi = anonMapPsi.getTypeRef();
      if (keyTypeRefPsi == null) throw new PsiProcessingException("Map key type not specified", psi, errors);
      TypeRef keyTypeRef = fromPsi(keyTypeRefPsi, errors);

      @Nullable SchemaValueTypeRef valueTypeRefPsi = anonMapPsi.getValueTypeRef();
      if (valueTypeRefPsi == null) throw new PsiProcessingException("Map value type not specified", psi, errors);
      ValueTypeRef valueTypeRef = fromPsi(valueTypeRefPsi, errors);

      return new AnonMapRef(keyTypeRef, valueTypeRef);
    }

    throw new PsiProcessingException("Unknown reference type: " + psi.getClass().getName(), psi, errors);
  }

  public static @NotNull ValueTypeRef fromPsi(@NotNull SchemaValueTypeRef psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    @Nullable SchemaDefaultOverride defaultOverridePsi = psi.getDefaultOverride();
    return new ValueTypeRef(
        fromPsi(psi.getTypeRef(), errors),
        defaultOverridePsi == null ? null : defaultOverridePsi.getVarTagRef().getQid().getCanonicalName()
    );
  }
}
