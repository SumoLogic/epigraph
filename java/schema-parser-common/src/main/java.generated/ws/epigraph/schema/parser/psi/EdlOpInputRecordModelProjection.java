// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOpInputRecordModelProjection extends PsiElement {

  @NotNull
  List<EdlOpInputFieldProjectionEntry> getOpInputFieldProjectionEntryList();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
