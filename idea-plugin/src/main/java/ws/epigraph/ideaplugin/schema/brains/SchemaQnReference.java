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

package ws.epigraph.ideaplugin.schema.brains;

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
import ws.epigraph.ideaplugin.schema.brains.hierarchy.CompletionTypeFilters;
import ws.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import ws.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import ws.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.*;
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
public class SchemaQnReference extends PsiReferenceBase<SchemaQnSegment> implements PsiPolyVariantReference {
  private final SchemaQnReferenceResolver resolver;

  private final ResolveCache.Resolver cachedResolver = (psiReference, incompleteCode) -> resolveImpl();
  private final ResolveCache.PolyVariantResolver<SchemaQnReference> polyVariantResolver =
      (schemaQnReference, incompleteCode) -> multiResolveImpl();

  public SchemaQnReference(SchemaQnSegment segment, SchemaQnReferenceResolver resolver) {
    super(segment);
    this.resolver = resolver;

    final int textOffset = segment.getTextRange().getStartOffset();
    final int nameTextOffset = segment.getTextOffset();

    setRangeInElement(new TextRange(
        nameTextOffset - textOffset,
        nameTextOffset + segment.getTextLength() - textOffset
    ));

  }

  public @NotNull SchemaQnReferenceResolver getResolver() {
    return resolver;
  }

  @Override
  public final @Nullable PsiElement resolve() {
    return ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, cachedResolver, true, false);
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    assert !(element instanceof SchemaTypeDefWrapper);
    return super.isReferenceTo(element);
  }

  private @Nullable PsiElement resolveImpl() {
    return resolver.resolve(myElement.getProject());
  }

  @Override
  public @NotNull ResolveResult[] multiResolve(boolean incompleteCode) {
    return ResolveCache.getInstance(myElement.getProject())
        .resolveWithCaching(this, polyVariantResolver, false, incompleteCode);
  }

  private @NotNull ResolveResult[] multiResolveImpl() {
    return resolver.multiResolve(myElement.getProject());
  }

  @Override
  public @NotNull Object[] getVariants() {
    final boolean isImport = isImport();
    final boolean isNamespaceDecl = isNamespaceDecl();
    final Project project = myElement.getProject();
    final GlobalSearchScope searchScope = SchemaSearchScopeUtil.getSearchScope(myElement);
    final Qn currentNamespace = NamespaceManager.getNamespace(getElement());

    final Qn input = resolver.getInput();
    final Qn inputPrefix = input.removeLastSegment();

    Set<SchemaTypeDef> typeDefVariants;
    Collection<SchemaNamespaceDecl> namespaceVariants;

    if (input.size() > 1) {
      // we already have multiple segments in the FQN

      Qn suffixPrefix = resolver.getSuffix().removeLastSegment();

      if (isNamespaceDecl) {
        typeDefVariants = Collections.emptySet();
      } else {
        List<Qn> namespacesToSearchForTypes = resolver.getPrefixes().stream().map(fqn -> fqn.append(suffixPrefix)).collect(Collectors.toList());
        typeDefVariants = SchemaIndexUtil.findTypeDefs(project, namespacesToSearchForTypes, null, searchScope).stream()
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
        typeDefVariants = SchemaIndexUtil
            .findTypeDefs(project, namespacesToSearchForTypes.toArray(new Qn[namespacesToSearchForTypes.size()]), searchScope)
            .stream()
            .filter(typeDef -> typeDef.getName() != null)
            .collect(Collectors.toSet());

        // all types in current NS
        typeDefVariants.addAll(SchemaIndexUtil.findTypeDefs(project, Collections.singletonList(currentNamespace), null, searchScope).stream()
            .filter(typeDef -> typeDef.getName() != null)
            .collect(Collectors.toSet()));

        // add standard imports
        typeDefVariants.addAll(SchemaIndexUtil.findTypeDefs(project, DEFAULT_IMPORTS, searchScope).stream()
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
                .withIcon(SchemaPresentationUtil.getIcon(typeDef))
                .withTypeText(SchemaPresentationUtil.getNamespaceString(typeDef, true)))
        .collect(Collectors.toSet());

    List<Qn> namespaceFqns = namespaceVariants.stream()
                                              .map(SchemaNamespaceDecl::getFqn)
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
    return PsiTreeUtil.getParentOfType(getElement(), SchemaImportStatement.class) != null;
  }

  private boolean isNamespaceDecl() {
    return PsiTreeUtil.getParentOfType(getElement(), SchemaNamespaceDecl.class) != null;
  }
}
