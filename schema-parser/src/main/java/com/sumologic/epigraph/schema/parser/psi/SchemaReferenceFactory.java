package com.sumologic.epigraph.schema.parser.psi;

import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaReferenceFactory {

  @Nullable
  public static PsiReference getFqnReference(@NotNull SchemaFqnSegment segment) {
    return null;
  }

  @Nullable
  public static PsiReference getVarTagReference(@NotNull SchemaVarTagRef varTagRef) {
    return null;
  }
}
