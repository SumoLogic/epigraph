// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOpDeleteRecordModelProjection extends PsiElement {

  @NotNull
  List<EdlOpDeleteFieldProjectionEntry> getOpDeleteFieldProjectionEntryList();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}