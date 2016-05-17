package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFqnReference extends PsiReferenceBase<SchemaFqnSegment> implements PsiPolyVariantReference {

  private final Collection<String> namespacesToSearch;
  private final String shortName;

  private final ResolveCache.Resolver resolver = (psiReference, incompleteCode) -> resolveImpl();
  private final ResolveCache.PolyVariantResolver<SchemaFqnReference> polyVariantResolver =
      (schemaFqnReference, incompleteCode) -> multiResolveImpl();

  public SchemaFqnReference(SchemaFqnSegment segment, Collection<Fqn> namespacesToSearch, Fqn suffix) {
    super(segment);

    final int textOffset = segment.getTextRange().getStartOffset();
    final int nameTextOffset = segment.getTextOffset();

    setRangeInElement(new TextRange(
        nameTextOffset - textOffset,
        nameTextOffset + segment.getTextLength() - textOffset
    ));

    if (suffix.isEmpty()) throw new IllegalArgumentException("Empty suffix for " + segment);

    if (suffix.size() == 1) {
      this.namespacesToSearch = namespacesToSearch.stream().map(Fqn::toString).collect(Collectors.toSet());
      this.shortName = suffix.toString();
    } else {
      final Fqn suffixPrefix = suffix.getPrefix();
      assert suffixPrefix != null;

      this.namespacesToSearch = namespacesToSearch.stream()
          .map(fqn -> fqn.append(suffixPrefix).toString()).collect(Collectors.toSet());
      this.namespacesToSearch.add(suffixPrefix.toString());
      this.shortName = suffix.getLast();
    }
  }

  @Nullable
  public final PsiElement resolve() {
    return ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, resolver, false, false);
  }

  @Nullable
  private PsiElement resolveImpl() {
    final Project project = myElement.getProject();
    PsiElement typeDef = SchemaIndexUtil.findTypeDef(project, namespacesToSearch, shortName);
    if (typeDef != null) return typeDef;

    Fqn prefix = getElement().getFqn();
    List<SchemaNamespaceDecl> namespaces = resolveNamespaces(prefix);
    if (namespaces.size() == 1) {
      SchemaNamespaceDecl namespaceDecl = namespaces.get(0);
      return getTargetSegment(namespaceDecl, prefix.size());
    }

    return null;
  }
  @NotNull
  @Override
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    return ResolveCache.getInstance(myElement.getProject())
        .resolveWithCaching(this, polyVariantResolver, false, incompleteCode);
  }

  @NotNull
  private ResolveResult[] multiResolveImpl() {
    final Project project = myElement.getProject();
    ResolveResult[] typeDefs = SchemaIndexUtil.findTypeDefs(project, namespacesToSearch, shortName).stream()
        .map(PsiElementResolveResult::new)
        .toArray(ResolveResult[]::new);

    Fqn prefix = getElement().getFqn();
    int prefixLength = prefix.size();
    List<SchemaNamespaceDecl> namespaceDecls = resolveNamespaces(prefix);

    ResolveResult[] namespaces = namespaceDecls.stream()
        .map(ns -> new PsiElementResolveResult(getTargetSegment(ns, prefixLength)))
        .toArray(ResolveResult[]::new);

    return ArrayUtil.mergeArrays(typeDefs, namespaces);
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    final Project project = myElement.getProject();
    Set<Object> typeRefVariants = SchemaIndexUtil.findTypeDefs(project, namespacesToSearch, null).stream()
        .filter(typeDef -> typeDef.getName() != null)
        .map(typeDef -> LookupElementBuilder.create(typeDef)
//            .withIcon(getTypeDefPresentationIcon())
            .withTypeText(getTypeDefNamespace(typeDef)))
        .collect(Collectors.toSet());

    String prefix = getElement().getFqn().removeLastSegment().toString(); // last segment is an error element
    Set<String> namespaceVariants = NamespaceManager.getNamespaceSegmentsWithPrefix(project, prefix);

    @SuppressWarnings("UnnecessaryLocalVariable")
    Set<Object> res = typeRefVariants;
    res.addAll(namespaceVariants);
    if (isImport()) res.add("*");

    return res.toArray();
  }

  /**
   * @return either a list with a single namespace declaration which is exactly our prefix, or a list
   * of namespaces that start with prefix
   */
  @NotNull
  private List<SchemaNamespaceDecl> resolveNamespaces(@NotNull Fqn prefix) {
    List<SchemaNamespaceDecl> namespaces = SchemaIndexUtil.findNamespaces(getElement().getProject(), prefix.toString());
    // try to find a namespace which is exactly our prefix
    for (SchemaNamespaceDecl namespace : namespaces) {
      //noinspection ConstantConditions
      if (namespace.getFqn().getFqn().equals(prefix))
        return Collections.singletonList(namespace);
    }

    return namespaces;
  }

  private SchemaFqnSegment getTargetSegment(@NotNull SchemaNamespaceDecl namespaceDecl, int prefixLength) {
    SchemaFqn fqn = namespaceDecl.getFqn();
    assert fqn != null;
    SchemaFqnSegment fqnSegment = fqn.getFqnSegmentList().get(prefixLength - 1);
    //noinspection ConstantConditions
    assert fqnSegment.getName().equals(getElement().getName());
    return fqnSegment;
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return getElement().setName(newElementName);
  }

  private String getTypeDefNamespace(@NotNull SchemaTypeDef typeDef) {
    String namespace = NamespaceManager.getNamespace(typeDef);
    return namespace == null ? typeDef.getContainingFile().getName() : namespace;
  }

  private boolean isImport() {
    return PsiTreeUtil.getParentOfType(getElement(), SchemaImportStatement.class) != null;
  }

//  private javax.swing.Icon getTypeDefPresentationIcon() {
//    return PlatformIcons.CLASS_ICON;
//  }
}
