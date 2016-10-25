// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlUpdateOperationDef extends PsiElement {

  @Nullable
  IdlOperationName getOperationName();

  @NotNull
  List<IdlUpdateOperationBodyPart> getUpdateOperationBodyPartList();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getUpdateOp();

}
