// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpInputRecordModelProjection extends IdlOpInputModelProjection {

  @NotNull
  List<IdlOpInputFieldProjection> getOpInputFieldProjectionList();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
