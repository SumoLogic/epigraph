// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlCustomOperationDef extends PsiElement {

  @NotNull
  List<EdlCustomOperationBodyPart> getCustomOperationBodyPartList();

  @Nullable
  EdlOperationName getOperationName();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getOpCustom();

}
