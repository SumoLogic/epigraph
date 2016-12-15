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

package ws.epigraph.edl.parser.psi;

import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import ws.epigraph.ideaplugin.edl.brains.NamespaceManager;
import ws.epigraph.ideaplugin.edl.brains.EdlQnReference;
import ws.epigraph.ideaplugin.edl.brains.EdlQnReferenceResolver;
import ws.epigraph.ideaplugin.edl.brains.EdlVarTagReference;
import ws.epigraph.ideaplugin.edl.index.EdlSearchScopeUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.refs.ImportAwareTypesResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ws.epigraph.ideaplugin.edl.brains.NamespaceManager.getNamespace;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 * @see <a href="https://github.com/SumoLogic/epigraph/wiki/References%20implementation#reference-resolution-algorithm">Reference resolution algorithm</a>
 */
public class EdlReferenceFactory {
  @Nullable
  public static PsiReference getQnReference(@NotNull EdlQnSegment segment) {
    EdlQnReferenceResolver resolver = getQnReferenceResolver(segment);

    return resolver == null ? null : new EdlQnReference(segment, resolver);
  }

  @Nullable
  public static EdlQnReferenceResolver getQnReferenceResolver(@NotNull EdlQnSegment segment) {
    final EdlFile file = (EdlFile) segment.getContainingFile();
    if (file == null) return null;

    final boolean isImport = PsiTreeUtil.getParentOfType(segment, EdlImportStatement.class) != null;

    return getQnReferenceResolver(file, segment.getQn(), isImport);
  }

  @Nullable
  public static EdlQnReferenceResolver getQnReferenceResolver(@NotNull EdlFile file,
                                                                 @NotNull Qn qn,
                                                                 boolean isImport) {
    if (qn.isEmpty()) return null;

    final List<Qn> prefixes;
    @Nullable Qn currentNamespace = getNamespace(file);

    if (isImport) {
      prefixes = new ArrayList<>();

      if (qn.size() == 1) {
        if (currentNamespace != null) prefixes.add(currentNamespace);
      } else {
        prefixes.add(Qn.EMPTY);
      }
    } else {
      prefixes = ImportAwareTypesResolver.calculateResolutionPrefixes(
          qn,
          currentNamespace,
          NamespaceManager.getImportedNamespaces(file),
          true
      );

      assert prefixes != null; // we know qn is non-empty
    }

    return  new EdlQnReferenceResolver(prefixes, qn, EdlSearchScopeUtil.getSearchScope(file));
  }

  @Nullable
  public static PsiReference getVarTagReference(@NotNull EdlVarTagRef varTagRef) {
    EdlValueTypeRef valueTypeRef = PsiTreeUtil.getParentOfType(varTagRef, EdlValueTypeRef.class);
    if (valueTypeRef == null) return null;

    EdlTypeRef varTypeRef = valueTypeRef.getTypeRef();
    if (varTypeRef instanceof EdlQnTypeRef) {
      EdlQnTypeRef fqnVarTypeRef = (EdlQnTypeRef) varTypeRef;
      EdlTypeDef typeDef = fqnVarTypeRef.resolve();
      if (typeDef instanceof EdlVarTypeDef) {
        EdlVarTypeDef varTypeDef = (EdlVarTypeDef) typeDef;
        return new EdlVarTagReference(varTypeDef, varTagRef.getQid());
      }
    }

    return null;
  }
}
