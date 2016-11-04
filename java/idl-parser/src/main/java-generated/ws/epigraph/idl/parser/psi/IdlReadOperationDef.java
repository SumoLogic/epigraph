// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlReadOperationDef extends PsiElement {

  @Nullable
  IdlOperationName getOperationName();

  @NotNull
  List<IdlReadOperationBodyPart> getReadOperationBodyPartList();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getRead();

}
