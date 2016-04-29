package com.sumologic.epigraph.ideaPlugin.schema.psi.references;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.PlatformIcons;
import com.sumologic.epigraph.ideaPlugin.schema.index.SchemaIndexUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaTypeReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

  private final Collection<String> namespacesToSearch;
  private final String shortName;

  public SchemaTypeReference(PsiElement element, Collection<String> namespacesToSearch, String shortName) {
    super(element);
    this.namespacesToSearch = namespacesToSearch;
    this.shortName = shortName;

    final int textOffset = element.getTextRange().getStartOffset();
    final int nameTextOffset = element.getTextOffset();

    setRangeInElement(new TextRange(
        nameTextOffset - textOffset,
        nameTextOffset + element.getTextLength() - textOffset
    ));

  }

  @NotNull
  @Override
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    final Project project = myElement.getProject();
    return SchemaIndexUtil.findTypeDefs(project, namespacesToSearch, shortName).stream()
        .map(PsiElementResolveResult::new)
        .toArray(ResolveResult[]::new);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    final Project project = myElement.getProject();
    return SchemaIndexUtil.findTypeDef(project, namespacesToSearch, shortName);
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    final Project project = myElement.getProject();
    return SchemaIndexUtil.findTypeDefs(project, namespacesToSearch, shortName).stream()
        .filter(typeDef -> typeDef.getName() != null)
        .map(typeDef -> LookupElementBuilder.create(typeDef)
            .withIcon(getPresentationIcon())
            .withTypeText(typeDef.getContainingFile().getName())) // TODO ?
        .toArray();
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    boolean res = super.isReferenceTo(element);
    return res;
  }

  private Icon getPresentationIcon() {
    // TODO
    return PlatformIcons.CLASS_ICON;
  }
}
