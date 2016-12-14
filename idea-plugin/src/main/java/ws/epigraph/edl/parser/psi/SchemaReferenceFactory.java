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
import ws.epigraph.ideaplugin.edl.brains.SchemaQnReference;
import ws.epigraph.ideaplugin.edl.brains.SchemaQnReferenceResolver;
import ws.epigraph.ideaplugin.edl.brains.SchemaVarTagReference;
import ws.epigraph.ideaplugin.edl.index.SchemaSearchScopeUtil;
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
public class SchemaReferenceFactory {
  @Nullable
  public static PsiReference getQnReference(@NotNull SchemaQnSegment segment) {
    SchemaQnReferenceResolver resolver = getQnReferenceResolver(segment);

    return resolver == null ? null : new SchemaQnReference(segment, resolver);
  }

  @Nullable
  public static SchemaQnReferenceResolver getQnReferenceResolver(@NotNull SchemaQnSegment segment) {
    final SchemaFile file = (SchemaFile) segment.getContainingFile();
    if (file == null) return null;

    final boolean isImport = PsiTreeUtil.getParentOfType(segment, SchemaImportStatement.class) != null;

    return getQnReferenceResolver(file, segment.getQn(), isImport);
  }

  @Nullable
  public static SchemaQnReferenceResolver getQnReferenceResolver(@NotNull SchemaFile file,
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

    return  new SchemaQnReferenceResolver(prefixes, qn, SchemaSearchScopeUtil.getSearchScope(file));
  }

  @Nullable
  public static PsiReference getVarTagReference(@NotNull SchemaVarTagRef varTagRef) {
    SchemaValueTypeRef valueTypeRef = PsiTreeUtil.getParentOfType(varTagRef, SchemaValueTypeRef.class);
    if (valueTypeRef == null) return null;

    SchemaTypeRef varTypeRef = valueTypeRef.getTypeRef();
    if (varTypeRef instanceof SchemaQnTypeRef) {
      SchemaQnTypeRef fqnVarTypeRef = (SchemaQnTypeRef) varTypeRef;
      SchemaTypeDef typeDef = fqnVarTypeRef.resolve();
      if (typeDef instanceof SchemaVarTypeDef) {
        SchemaVarTypeDef varTypeDef = (SchemaVarTypeDef) typeDef;
        return new SchemaVarTagReference(varTypeDef, varTagRef.getQid());
      }
    }

    return null;
  }
}
