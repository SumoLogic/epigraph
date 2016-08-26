package com.sumologic.epigraph.ideaplugin.schema.features.usages;

import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import io.epigraph.lang.parser.psi.SchemaFqnSegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaElementDescriptionProvider implements ElementDescriptionProvider {
  @Nullable
  @Override
  public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
    // https://intellij-support.jetbrains.com/hc/en-us/community/posts/206765785-Custom-name-for-Find-Usages-of-symbol
    if (location instanceof UsageViewLongNameLocation) {

      if (element instanceof EpigraphTypeDef) {
        EpigraphTypeDef epigraphTypeDef = (EpigraphTypeDef) element;
        return SchemaPresentationUtil.getName(epigraphTypeDef, true);
      }

      if (element instanceof SchemaFqnSegment) {
        SchemaFqnSegment fqnSegment = (SchemaFqnSegment) element;
        return fqnSegment.getFqn().toString();
      }
    }

    return null;
  }
}
