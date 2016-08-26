// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EpigraphOpOutputRecordModelProjection extends PsiElement {

  @NotNull
  List<EpigraphOpOutputFieldProjection> getOpOutputFieldProjectionList();

  @NotNull
  List<EpigraphOpOutputRecordPolyBranch> getOpOutputRecordPolyBranchList();

  @NotNull
  PsiElement getParenLeft();

  @NotNull
  PsiElement getParenRight();

}
