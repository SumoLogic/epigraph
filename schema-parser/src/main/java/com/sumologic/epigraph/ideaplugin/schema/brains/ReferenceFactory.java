package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.psi.PsiReference;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnSegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ReferenceFactory {

  @Nullable
  public static PsiReference getReference(@NotNull SchemaFqnSegment segment) {
    return null;
  }
}
