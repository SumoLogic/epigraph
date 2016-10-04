package com.sumologic.epigraph.ideaplugin.schema.features.usages;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import io.epigraph.schema.parser.psi.SchemaFile;
import io.epigraph.schema.parser.psi.SchemaQnSegment;
import io.epigraph.schema.parser.psi.SchemaImportStatement;
import io.epigraph.schema.parser.psi.SchemaVarTagRef;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaUsageTypeProvider implements UsageTypeProvider {
  @Nullable
  @Override
  public UsageType getUsageType(PsiElement element) {
    final PsiFile psiFile = element.getContainingFile();

    if (!(psiFile instanceof SchemaFile)) return null;

    if (element instanceof SchemaQnSegment) {
      if (PsiTreeUtil.getParentOfType(element, SchemaImportStatement.class) != null) {
        return IMPORT_USAGE_TYPE;
      }
      return TYPE_REF_USAGE_TYPE; // be more precise: extends, list getElement type etc ?
    }

    if (PsiTreeUtil.getParentOfType(element, SchemaVarTagRef.class) != null)
      return VAR_TAG_USAGE_TYPE;

    return null;
  }

  private static final UsageType TYPE_REF_USAGE_TYPE = new UsageType("Type reference");
  private static final UsageType IMPORT_USAGE_TYPE = new UsageType("Import statement");
  private static final UsageType VAR_TAG_USAGE_TYPE = new UsageType("Default override");
}
