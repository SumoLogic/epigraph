// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EpigraphOpOutputVarProjection extends PsiElement {

  @Nullable
  EpigraphOpOutputModelProjection getOpOutputModelProjection();

  @NotNull
  List<EpigraphOpOutputTagProjection> getOpOutputTagProjectionList();

  @Nullable
  EpigraphQid getQid();

  @Nullable
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
