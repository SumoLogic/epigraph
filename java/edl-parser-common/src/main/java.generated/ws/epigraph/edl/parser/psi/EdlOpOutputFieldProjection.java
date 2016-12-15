// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOpOutputFieldProjection extends PsiElement {

  @NotNull
  List<EdlOpOutputFieldProjectionBodyPart> getOpOutputFieldProjectionBodyPartList();

  @NotNull
  EdlOpOutputVarProjection getOpOutputVarProjection();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
