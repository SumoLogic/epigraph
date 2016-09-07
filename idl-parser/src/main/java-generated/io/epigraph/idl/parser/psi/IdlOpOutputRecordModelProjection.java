// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpOutputRecordModelProjection extends IdlOpOutputModelProjection {

  @NotNull
  List<IdlOpOutputFieldProjection> getOpOutputFieldProjectionList();

  @NotNull
  List<IdlOpOutputRecordPolyBranch> getOpOutputRecordPolyBranchList();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
