package com.sumologic.dohyo.plugin.schema.psi.references;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.PsiElementResult;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.PlatformIcons;
import com.sumologic.dohyo.plugin.schema.index.SchemaIndexUtil;
import com.sumologic.dohyo.plugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaTypeReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
  //  private
  private String typeName;
  private Class<? extends SchemaTypeDef> kind;

  public SchemaTypeReference(PsiElement element, String typeName, Class<? extends SchemaTypeDef> kind) {
    super(element);

    final int textOffset = element.getTextRange().getStartOffset();
    final int nameTextOffset = element.getTextOffset();

    setRangeInElement(new TextRange(
        nameTextOffset - textOffset,
        nameTextOffset + element.getTextLength() - textOffset
    ));

    this.kind = kind;
    this.typeName = typeName;
  }

  @NotNull
  @Override
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    final Project project = myElement.getProject();
    return SchemaIndexUtil.findTypeDefs(project, typeName, null).stream() // TODO kind
        .map(PsiElementResolveResult::new)
        .toArray(ResolveResult[]::new);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    // TODO optimize
    ResolveResult[] resolveResults = multiResolve(false);
    return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    final Project project = myElement.getProject();
    return SchemaIndexUtil.findTypeDefs(project, typeName, kind).stream()
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
