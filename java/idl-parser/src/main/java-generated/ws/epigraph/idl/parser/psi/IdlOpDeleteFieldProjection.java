// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpDeleteFieldProjection extends PsiElement {

  @NotNull
  List<IdlOpDeleteFieldProjectionBodyPart> getOpDeleteFieldProjectionBodyPartList();

  @NotNull
  IdlOpDeleteVarProjection getOpDeleteVarProjection();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
