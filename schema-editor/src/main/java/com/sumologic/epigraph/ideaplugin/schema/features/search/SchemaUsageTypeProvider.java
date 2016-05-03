package com.sumologic.epigraph.ideaplugin.schema.features.search;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFile;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnSegment;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaUsageTypeProvider implements UsageTypeProvider {
  @Nullable
  @Override
  public UsageType getUsageType(PsiElement element) {
    final PsiFile psiFile = element.getContainingFile();
    if (psiFile instanceof SchemaFile) {
      if (element instanceof SchemaFqnSegment) return TYPE_REF_USAGE_TYPE; // be more precise: extends, list element type etc ?
    }
    return null;
  }

  private static final UsageType TYPE_REF_USAGE_TYPE = new UsageType("Type reference");
}
