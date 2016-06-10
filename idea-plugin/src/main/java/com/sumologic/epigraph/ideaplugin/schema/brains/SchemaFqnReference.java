package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.psi.SchemaFqnSegment;
import com.sumologic.epigraph.schema.parser.psi.SchemaImportStatement;
import com.sumologic.epigraph.schema.parser.psi.SchemaNamespaceDecl;
import com.sumologic.epigraph.schema.parser.psi.SchemaTypeDefWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFqnReference extends PsiReferenceBase<SchemaFqnSegment> implements PsiPolyVariantReference {
  private final SchemaFqnReferenceResolver resolver;

  private final ResolveCache.Resolver cachedResolver = (psiReference, incompleteCode) -> resolveImpl();
  private final ResolveCache.PolyVariantResolver<SchemaFqnReference> polyVariantResolver =
      (schemaFqnReference, incompleteCode) -> multiResolveImpl();

  public SchemaFqnReference(SchemaFqnSegment segment, SchemaFqnReferenceResolver resolver) {
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
  public SchemaFqnReferenceResolver getResolver() {
    return resolver;
  }

  @Nullable
  public final PsiElement resolve() {
    return ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, cachedResolver, false, false);
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    assert !(element instanceof SchemaTypeDefWrapper);
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
    final Project project = myElement.getProject();
    final Fqn currentNamespace = NamespaceManager.getNamespace(getElement());

    Set<String> namespacesToSearchStr = new HashSet<>(resolver.getNamespacesToSearchStr());
    // add input's prefix, may be it's a full FQN?
    Fqn inputPrefix = resolver.getInput().removeLastSegment();

    if (!inputPrefix.isEmpty()) {
      namespacesToSearchStr.add(inputPrefix.toString());
    }

    Set<Object> typeRefVariants = SchemaIndexUtil.findTypeDefs(project, namespacesToSearchStr, null).stream()
        .filter(typeDef ->
            // don't suggest to import types from the same namespace
            typeDef.getName() != null && (!isImport || currentNamespace == null || !currentNamespace.equals(NamespaceManager.getNamespace(typeDef)))
        ).map(typeDef ->
            LookupElementBuilder.create(typeDef) // TODO use presentation utils
                .withIcon(SchemaPresentationUtil.getIcon(typeDef))
                .withTypeText(SchemaPresentationUtil.getNamespaceString(typeDef)))
        .collect(Collectors.toSet());

    // complete namespaces
    Collection<SchemaNamespaceDecl> namespaces;
    if (inputPrefix.isEmpty()) {
      namespaces = NamespaceManager.getNamespaceManager(project).getAllNamespaces();
    } else {
      namespaces = NamespaceManager.getNamespacesByPrefix(project, inputPrefix, false);
    }

    List<Fqn> namespaceFqns = namespaces.stream()
        .map(SchemaNamespaceDecl::getFqn2)
        .filter(fqn -> !fqn.equals(currentNamespace)) // not interested in current namespace
        .collect(Collectors.toList());

    Set<String> namespaceVariants = Fqn.getMatchingWithPrefixRemoved(namespaceFqns, inputPrefix)
        .stream()
        .map(Fqn::first) // only leave next segment after removing matching prefix
        .filter(s -> s != null && s.length() > 0)
        .collect(Collectors.toSet());

    @SuppressWarnings("UnnecessaryLocalVariable")
    Set<Object> res = typeRefVariants;
    res.addAll(namespaceVariants);

    return res.toArray();
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return getElement().setName(newElementName);
  }

  private boolean isImport() {
    return PsiTreeUtil.getParentOfType(getElement(), SchemaImportStatement.class) != null;
  }
}
