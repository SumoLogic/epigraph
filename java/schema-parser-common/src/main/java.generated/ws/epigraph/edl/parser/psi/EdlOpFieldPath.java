// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOpFieldPath extends PsiElement {

  @NotNull
  List<EdlOpFieldPathBodyPart> getOpFieldPathBodyPartList();

  @Nullable
  EdlOpVarPath getOpVarPath();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
