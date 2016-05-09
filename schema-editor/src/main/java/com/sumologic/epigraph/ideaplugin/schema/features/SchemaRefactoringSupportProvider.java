package com.sumologic.epigraph.ideaplugin.schema.features;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnSegment;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnTypeRef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaRefactoringSupportProvider extends RefactoringSupportProvider {
  @Override
  public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, @Nullable PsiElement context) {
    return element instanceof SchemaTypeDef || element instanceof SchemaFqnSegment;
  }

  @Override
  public boolean isSafeDeleteAvailable(@NotNull PsiElement element) {
    return element instanceof SchemaTypeDef;
  }
}
