/*
 * Copyright 2018 Sumo Logic
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

package ws.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import ws.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.SchemaNamespaceDecl;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaQnReferenceResolver {
  private final @NotNull List<Qn> prefixes;
  private final @NotNull Qn suffix;
  private final @NotNull Qn input;
  private final @NotNull GlobalSearchScope searchScope;

  public SchemaQnReferenceResolver(@NotNull List<Qn> prefixes, @NotNull Qn input, @NotNull GlobalSearchScope searchScope) {
    this.searchScope = searchScope;
    if (input.isEmpty()) throw new IllegalArgumentException("Empty input");

    this.input = input;
    this.prefixes = prefixes;
    this.suffix = input;
  }

  public @NotNull Qn getInput() {
    return input;
  }

  public @NotNull Qn getSuffix() {
    return suffix;
  }

  public @NotNull List<Qn> getPrefixes() {
    return prefixes;
  }

  public @Nullable PsiElement resolve(@NotNull Project project) {
    SchemaTypeDef typeDef = resolveTypeDef(project);
    if (typeDef != null) return typeDef;

    // we can't find a typedef by this reference, lets check if it points to a namespace declaration

    // type name input (which we tried to append to different prefixes) now becomes
    // source namespace's prefix

    Qn prefix = input;
    List<SchemaNamespaceDecl> namespaces = resolveNamespaces(project, prefix);
    if (namespaces.size() == 1) {
      SchemaNamespaceDecl namespaceDecl = namespaces.get(0);
      return getTargetSegment(namespaceDecl, prefix.size());
    }

    return null;
  }

  public @Nullable Qn getTargetTypeDefQn(@NotNull Project project) {
    SchemaTypeDef typeDef = resolveTypeDef(project);
    if (typeDef != null) return typeDef.getQn();
    return null;
  }

  private @Nullable SchemaTypeDef resolveTypeDef(@NotNull Project project) {
    return SchemaIndexUtil.findTypeDef(project, prefixes, suffix, searchScope);
//    return SchemaIndexUtil.findSingleTypeDef(project, prefixes, suffix, searchScope);
  }

  public @NotNull ResolveResult[] multiResolve(@NotNull Project project) {
    // see comment in `resolve` above re. namespace declaration reference

    Qn prefix = input;
    int prefixLength = prefix.size();
    List<SchemaNamespaceDecl> namespaceDecls = resolveNamespaces(project, prefix);

    ResolveResult[] namespaces = namespaceDecls.stream()
        .map(ns -> new PsiElementResolveResult(getTargetSegment(ns, prefixLength)))
        .toArray(ResolveResult[]::new);

    return ArrayUtil.mergeArrays(SchemaIndexUtil.findTypeDefs(project, prefixes, suffix, searchScope)
        .stream()
        .filter(Objects::nonNull)
        .map(PsiElementResolveResult::new)
        .toArray(ResolveResult[]::new), namespaces);
  }

  /**
   * @return either a list with a single namespace declaration which is exactly our prefix, or a list
   * of namespaces that start with prefix
   */
  private @NotNull List<SchemaNamespaceDecl> resolveNamespaces(@NotNull Project project, @NotNull Qn prefix) {
    List<SchemaNamespaceDecl> namespaces = SchemaIndexUtil.findNamespaces(project, prefix.toString(), searchScope);
    // try to find a namespace which is exactly our prefix
    for (SchemaNamespaceDecl namespace : namespaces) {
      //noinspection ConstantConditions
      if (namespace.getFqn().equals(prefix))
        return Collections.singletonList(namespace);
    }

    return namespaces;
  }

  private PsiElement getTargetSegment(@NotNull SchemaNamespaceDecl namespaceDecl, @SuppressWarnings("UnusedParameters") int prefixLength) {
    // This forces PSI tree re-parse. Adding stubs for SchemaFqn and SchemaFqnSegment is one option.
    // Just pointing to the namespace decl is another

//    SchemaFqn fqn = namespaceDecl.getFqn();
//    assert fqn != null;
//    //noinspection ConstantConditions
////    assert fqnSegment.getName().equals(getElement().getName());
//    return fqn.getFqnSegmentList().get(prefixLength - 1);

    return namespaceDecl;
  }

}
