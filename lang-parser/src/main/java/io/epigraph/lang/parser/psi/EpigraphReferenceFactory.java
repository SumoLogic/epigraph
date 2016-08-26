package io.epigraph.lang.parser.psi;

import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphReferenceFactory {

  @Nullable
  public static PsiReference getFqnReference(@NotNull EpigraphFqnSegment segment) {
    return null;
  }

  @Nullable
  public static PsiReference getVarTagReference(@NotNull EpigraphVarTagRef varTagRef) {
    return null;
  }
}
