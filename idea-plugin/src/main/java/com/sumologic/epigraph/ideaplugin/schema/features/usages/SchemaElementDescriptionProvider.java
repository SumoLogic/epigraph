package com.sumologic.epigraph.ideaplugin.schema.features.usages;

import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import io.epigraph.schema.parser.psi.SchemaQnSegment;
import io.epigraph.schema.parser.psi.SchemaTypeDef;
import io.epigraph.schema.parser.psi.SchemaVarTagRef;
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
    if (location instanceof UsageViewLongNameLocation ||
        location instanceof UsageViewShortNameLocation) {

      if (element instanceof SchemaTypeDef) {
        SchemaTypeDef schemaTypeDef = (SchemaTypeDef) element;
        return SchemaPresentationUtil.getName(schemaTypeDef, true);
      }

      if (element instanceof SchemaQnSegment) {
        SchemaQnSegment fqnSegment = (SchemaQnSegment) element;
        return fqnSegment.getQn().toString();
      }

      if (element instanceof SchemaVarTagRef) {
        SchemaVarTagRef tagRef = (SchemaVarTagRef) element;
        return tagRef.getQid().getText();
      }
    }

    return null;
  }
}
