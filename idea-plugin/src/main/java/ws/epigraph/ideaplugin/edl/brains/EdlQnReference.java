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

package ws.epigraph.ideaplugin.edl.brains;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import ws.epigraph.ideaplugin.edl.brains.hierarchy.CompletionTypeFilters;
import ws.epigraph.ideaplugin.edl.index.EdlIndexUtil;
import ws.epigraph.ideaplugin.edl.index.EdlSearchScopeUtil;
import ws.epigraph.ideaplugin.edl.presentation.EdlPresentationUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.edl.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ws.epigraph.lang.DefaultImports.DEFAULT_IMPORTS;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 * @see <a href="https://github.com/SumoLogic/epigraph/wiki/References%20implementation#reference-resolution-algorithm">Reference resolution algorithm</a>
 */
public class EdlQnReference extends PsiReferenceBase<EdlQnSegment> implements PsiPolyVariantReference {
  private final EdlQnReferenceResolver resolver;

  private final ResolveCache.Resolver cachedResolver = (psiReference, incompleteCode) -> resolveImpl();
  private final ResolveCache.PolyVariantResolver<EdlQnReference> polyVariantResolver =
      (edlQnReference, incompleteCode) -> multiResolveImpl();

  public EdlQnReference(EdlQnSegment segment, EdlQnReferenceResolver resolver) {
    super(segment);
    this.resolver = resolver;

    final int textOffset = segment.getTextRange().getStartOffset();
    final int nameTextOffset = segment.getTextOffset();

    setRangeInElement(new TextRange(
        nameTextOffset - textOffset,
        nameTextOffset + segment.getTextLength() - textOffset
    ));

  }

  @NotNull
  public EdlQnReferenceResolver getResolver() {
    return resolver;
  }

  @Override
  @Nullable
  public final PsiElement resolve() {
    return ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, cachedResolver, false, false);
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    assert !(element instanceof EdlTypeDefWrapper);
    return super.isReferenceTo(element);
  }

  @Nullable
  private PsiElement resolveImpl() {
    return resolver.resolve(myElement.getProject());
  }

  @NotNull
  @Override
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    return ResolveCache.getInstance(myElement.getProject())
        .resolveWithCaching(this, polyVariantResolver, false, incompleteCode);
  }

  @NotNull
  private ResolveResult[] multiResolveImpl() {
    return resolver.multiResolve(myElement.getProject());
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    final boolean isImport = isImport();
    final boolean isNamespaceDecl = isNamespaceDecl();
    final Project project = myElement.getProject();
    final GlobalSearchScope searchScope = EdlSearchScopeUtil.getSearchScope(myElement);
    final Qn currentNamespace = NamespaceManager.getNamespace(getElement());

    final Qn input = resolver.getInput();
    final Qn inputPrefix = input.removeLastSegment();

    Set<EdlTypeDef> typeDefVariants;
    Collection<EdlNamespaceDecl> namespaceVariants;

    if (input.size() > 1) {
      // we already have multiple segments in the FQN

      Qn suffixPrefix = resolver.getSuffix().removeLastSegment();

      if (isNamespaceDecl) {
        typeDefVariants = Collections.emptySet();
      } else {
        List<Qn> namespacesToSearchForTypes = resolver.getPrefixes().stream().map(fqn -> fqn.append(suffixPrefix)).collect(Collectors.toList());
        typeDefVariants = EdlIndexUtil.findTypeDefs(project, namespacesToSearchForTypes, null, searchScope).stream()
            .filter(typeDef ->
                // don't suggest to import types from the same namespace
                typeDef.getName() != null && (!isImport || currentNamespace == null || !currentNamespace.equals(NamespaceManager.getNamespace(typeDef))))
            .collect(Collectors.toSet());
      }

      // complete namespaces
      namespaceVariants = NamespaceManager.getNamespacesByPrefix(project, inputPrefix, false, searchScope);
    } else {
      // we have only one segment in the FQN

      List<Qn> namespacesToSearchForTypes = NamespaceManager.getImportedNamespaces(myElement);

      if (!isImport && !isNamespaceDecl) {
        typeDefVariants = EdlIndexUtil
            .findTypeDefs(project, namespacesToSearchForTypes.toArray(new Qn[namespacesToSearchForTypes.size()]), searchScope)
            .stream()
            .filter(typeDef -> typeDef.getName() != null)
            .collect(Collectors.toSet());

        // all types in current NS
        typeDefVariants.addAll(EdlIndexUtil.findTypeDefs(project, Collections.singletonList(currentNamespace), null, searchScope).stream()
            .filter(typeDef -> typeDef.getName() != null)
            .collect(Collectors.toSet()));

        // add standard imports
        typeDefVariants.addAll(EdlIndexUtil.findTypeDefs(project, DEFAULT_IMPORTS, searchScope).stream()
            .filter(typeDef -> typeDef.getName() != null)
            .collect(Collectors.toSet()));

      } else {
        // don't suggest imported types in another imports
        typeDefVariants = Collections.emptySet();
      }

      // complete namespaces
      namespaceVariants = NamespaceManager.getNamespaceManager(project).getAllNamespaces(searchScope);
    }

    Set<Object> typeDefElements = typeDefVariants.stream()
        .filter(CompletionTypeFilters.combined(getElement()))
        .map(typeDef ->
            LookupElementBuilder.create(typeDef)
                .withIcon(EdlPresentationUtil.getIcon(typeDef))
                .withTypeText(EdlPresentationUtil.getNamespaceString(typeDef, true)))
        .collect(Collectors.toSet());

    List<Qn> namespaceFqns = namespaceVariants.stream()
                                              .map(EdlNamespaceDecl::getFqn)
                                              .filter(fqn -> fqn != null && !fqn.equals(currentNamespace)) // not interested in current namespace
                                              .collect(Collectors.toList());

    Set<String> namespaceElements = Qn.getMatchingWithPrefixRemoved(namespaceFqns, inputPrefix)
                                      .stream()
                                      .map(Qn::first) // only leave next segment after removing matching prefix
                                      .filter(s -> s != null && !s.isEmpty())
                                      .collect(Collectors.toSet());

    @SuppressWarnings("UnnecessaryLocalVariable")
    Set<Object> res = typeDefElements;
    res.addAll(namespaceElements);

    return res.toArray();
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return getElement().setName(newElementName);
  }

  private boolean isImport() {
    return PsiTreeUtil.getParentOfType(getElement(), EdlImportStatement.class) != null;
  }

  private boolean isNamespaceDecl() {
    return PsiTreeUtil.getParentOfType(getElement(), EdlNamespaceDecl.class) != null;
  }
}
