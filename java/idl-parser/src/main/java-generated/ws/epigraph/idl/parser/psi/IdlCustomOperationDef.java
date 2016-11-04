// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlCustomOperationDef extends PsiElement {

  @NotNull
  List<IdlCustomOperationBodyPart> getCustomOperationBodyPartList();

  @NotNull
  IdlOperationName getOperationName();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getCustom();

}
