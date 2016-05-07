package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PlatformIcons;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFqnReference extends PsiReferenceBase<SchemaFqnSegment> implements PsiPolyVariantReference {

  private final Collection<String> namespacesToSearch;
  private final String shortName;

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

  @NotNull
  @Override
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    final Project project = myElement.getProject();
    ResolveResult[] typeDefs = SchemaIndexUtil.findTypeDefs(project, namespacesToSearch, shortName).stream()
        .map(PsiElementResolveResult::new)
        .toArray(ResolveResult[]::new);

    List<SchemaNamespaceDecl> namespaceDecls = resolveNamespaces(project);

    ResolveResult[] namespaces = namespaceDecls.stream()
        .map(PsiElementResolveResult::new)
        .toArray(ResolveResult[]::new);

    return ArrayUtil.mergeArrays(typeDefs, namespaces);
  }

  @NotNull
  private List<SchemaNamespaceDecl> resolveNamespaces(Project project) {
    SchemaFqnSegment fqnSegment = getElement();
    SchemaFqn schemaFqn = (SchemaFqn) fqnSegment.getParent();
    assert schemaFqn != null;

    List<SchemaFqnSegment> segmentList = schemaFqn.getFqnSegmentList();
    StringBuilder prefix = new StringBuilder();

    for (SchemaFqnSegment segment : segmentList) {
      if (prefix.length() > 0) prefix.append('.');
      prefix.append(segment.getText());
      if (segment == fqnSegment) break;
    }

    String prefixString = prefix.toString();

    List<SchemaNamespaceDecl> namespaces = SchemaIndexUtil.findNamespaces(project, prefixString);
    // try to find a namespace which is exactly our prefix
    for (SchemaNamespaceDecl namespace : namespaces) {
      //noinspection ConstantConditions
      if (namespace.getFqn().getFqn().toString().equals(prefixString))
        return Collections.singletonList(namespace);
    }

    return namespaces;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    final Project project = myElement.getProject();
    PsiElement typeDef = SchemaIndexUtil.findTypeDef(project, namespacesToSearch, shortName);
    if (typeDef != null) return typeDef;

    List<SchemaNamespaceDecl> namespaces = resolveNamespaces(myElement.getProject());
    if (namespaces.size() == 1) return namespaces.get(0);

    return null;
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return getElement().setName(newElementName);
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    final Project project = myElement.getProject();
    Set<Object> typeRefVariants = SchemaIndexUtil.findTypeDefs(project, namespacesToSearch, null).stream()
        .filter(typeDef -> typeDef.getName() != null)
        .map(typeDef -> LookupElementBuilder.create(typeDef)
            .withIcon(getTypeDefPresentationIcon())
            .withTypeText(getTypeDefNamespace(typeDef)))
        .collect(Collectors.toSet());

    SchemaFqnSegment fqnSegment = getElement();
    SchemaFqn schemaFqn = (SchemaFqn) fqnSegment.getParent();
    Fqn fqn = schemaFqn.getFqn();
    String prefix = fqn.removeLastSegment().toString();
    Set<String> namespaceVariants = NamespaceManager.getNamespaceSegmentsWithPrefix(project, prefix);

    @SuppressWarnings("UnnecessaryLocalVariable")
    Set<Object> res = typeRefVariants;
    res.addAll(namespaceVariants);
    if (isImport()) res.add("*");

    return res.toArray();
  }

  private String getTypeDefNamespace(@NotNull SchemaTypeDef typeDef) {
    String namespace = NamespaceManager.getNamespace(typeDef);
    return namespace == null ? typeDef.getContainingFile().getName() : namespace;
  }

  private boolean isImport() {
    return PsiTreeUtil.getParentOfType(getElement(), SchemaImportStatement.class) != null;
  }

  private Icon getTypeDefPresentationIcon() {
    // TODO
    return PlatformIcons.CLASS_ICON;
  }
}
