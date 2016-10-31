// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlCreateOperationDef extends PsiElement {

  @NotNull
  List<IdlCreateOperationBodyPart> getCreateOperationBodyPartList();

  @Nullable
  IdlOperationName getOperationName();

  @NotNull
  PsiElement getCreateOp();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
