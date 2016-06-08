package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFqnReference extends PsiReferenceBase<SchemaFqnSegment> implements PsiPolyVariantReference {
  private final SchemaFqnReferenceResolver resolver;
  private final List<Fqn> visibleFqns;

  private final ResolveCache.Resolver cachedResolver = (psiReference, incompleteCode) -> resolveImpl();
  private final ResolveCache.PolyVariantResolver<SchemaFqnReference> polyVariantResolver =
      (schemaFqnReference, incompleteCode) -> multiResolveImpl();

  public SchemaFqnReference(SchemaFqnSegment segment, SchemaFqnReferenceResolver resolver, List<Fqn> visibleFqns) {
    super(segment);
    this.resolver = resolver;
    this.visibleFqns = visibleFqns;

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
    // TODO: namespace references in imports should see all namespaces
    // in all other places: only current+imported namespaces

    final boolean isImport = isImport();
    final Project project = myElement.getProject();
    final Fqn currentNamespace = NamespaceManager.getNamespace(getElement());

    // resolver.fqns = all visible fqns such that their last segment = resolver.fqn.first(), with last segment removed and resolver.fqn appended
    // for example if there's 'foo.bar' import and fqn='bar.Baz' and resolver.fqns will contain 'foo.Baz'

    // now if completion was invoked then last segment of fqn will contain a dummy token like 'bar.BIntellijIdeaRulezz'
    // so we have to take all FQNs and remove last segment from them, then look for all types/namespaces in the resulting namespace.

    // there's and edge case though, what if resolver.fqn had only one segment? It would look like 'BIntelljIdeaRulezz'. If
    // visible FQNs had an (e.g. import) 'foo.bar.Baz' then it will be filtered out, because 'Baz' != 'BIntellijIdeaRulezz'
    // and so resolver.fqns list will be empty. But we still want completion, so pre-filtered list of visible FQNs is saved and passed to us as
    // visibleFqns.

    List<Fqn> fqns = resolver.getSourceFqn().size() == 1 ? visibleFqns : Arrays.asList(resolver.getFqns());

    Set<String> typeNamespaces = fqns.stream()
        .map(fqn -> fqn.isEmpty() ? null : fqn.removeLastSegment().toString())
        .filter(s -> s != null)
        .collect(Collectors.toSet());

    Set<Object> typeRefVariants = SchemaIndexUtil.findTypeDefs(project, typeNamespaces, null).stream()
        .filter(typeDef ->
            // don't suggest to import types from the same namespace
            typeDef.getName() != null && (!isImport || currentNamespace == null || !currentNamespace.equals(NamespaceManager.getNamespace(typeDef)))
        ).map(typeDef ->
            LookupElementBuilder.create(typeDef) // TODO use presentation utils
                .withIcon(SchemaPresentationUtil.getIcon(typeDef))
                .withTypeText(SchemaPresentationUtil.getNamespaceString(typeDef)))
        .collect(Collectors.toSet());

    // add namespaces as variants
    Fqn prefix = getElement().getFqn().removeLastSegment(); // last segment is an error getElement, so we have to remove it
    List<Fqn> namespaces = isImport
        ? NamespaceManager.getNamespacesByPrefix(project, prefix, false).stream().map(SchemaNamespaceDecl::getFqn2).collect(Collectors.toList())
        : NamespaceManager.getImportedNamespaces(getElement());

    Set<String> namespaceVariants = Fqn.getMatchingWithPrefixRemoved(namespaces, prefix)
        .stream().map(Fqn::toString).collect(Collectors.toSet());

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
