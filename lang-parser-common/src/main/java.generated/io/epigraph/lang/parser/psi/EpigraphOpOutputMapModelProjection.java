// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EpigraphOpOutputMapModelProjection extends PsiElement {

  @NotNull
  EpigraphOpOutputKeyProjection getOpOutputKeyProjection();

  @Nullable
  EpigraphOpOutputMapPolyBranch getOpOutputMapPolyBranch();

  @Nullable
  EpigraphOpOutputVarProjection getOpOutputVarProjection();

  @Nullable
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
