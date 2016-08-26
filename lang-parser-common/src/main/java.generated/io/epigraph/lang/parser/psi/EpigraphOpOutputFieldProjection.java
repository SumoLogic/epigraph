// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EpigraphOpOutputFieldProjection extends PsiElement {

  @Nullable
  EpigraphOpOutputFieldProjectionBody getOpOutputFieldProjectionBody();

  @Nullable
  EpigraphOpOutputVarProjection getOpOutputVarProjection();

  @NotNull
  EpigraphQid getQid();

  @Nullable
  PsiElement getColon();

}
