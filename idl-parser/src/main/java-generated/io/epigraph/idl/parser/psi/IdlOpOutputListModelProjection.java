// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpOutputListModelProjection extends IdlOpOutputModelProjection {

  @NotNull
  List<IdlOpOutputListPolyBranch> getOpOutputListPolyBranchList();

  @Nullable
  IdlOpOutputVarProjection getOpOutputVarProjection();

  @NotNull
  PsiElement getStar();

}
