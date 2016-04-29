package com.sumologic.epigraph.ideaplugin.schema.psi.references;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.PlatformIcons;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.brains.Fqn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaTypeReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

  private final Collection<String> namespacesToSearch;
  private final String shortName;

  public SchemaTypeReference(PsiElement element, Collection<Fqn> namespacesToSearch, Fqn suffix) {
    super(element);

    final int textOffset = element.getTextRange().getStartOffset();
    final int nameTextOffset = element.getTextOffset();

    setRangeInElement(new TextRange(
        nameTextOffset - textOffset,
        nameTextOffset + element.getTextLength() - textOffset
    ));

    if (suffix.isEmpty()) throw new IllegalArgumentException("Empty suffix for " + element);

    if (suffix.size() == 1) {
      this.namespacesToSearch = namespacesToSearch.stream().map(Fqn::toString).collect(Collectors.toSet());
      this.shortName = suffix.toString();
    } else {
      final Fqn suffixPrefix = suffix.getPrefix();
      assert suffixPrefix != null;

      this.namespacesToSearch = namespacesToSearch.stream()
          .map(fqn -> fqn.append(suffixPrefix).toString()).collect(Collectors.toSet());
      this.shortName = suffix.getLast();
    }
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

  private Icon getPresentationIcon() {
    // TODO
    return PlatformIcons.CLASS_ICON;
  }
}
