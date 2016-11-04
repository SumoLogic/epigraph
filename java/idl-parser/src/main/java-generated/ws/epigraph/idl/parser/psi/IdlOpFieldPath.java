// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpFieldPath extends PsiElement {

  @NotNull
  List<IdlOpFieldPathBodyPart> getOpFieldPathBodyPartList();

  @Nullable
  IdlOpVarPath getOpVarPath();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
