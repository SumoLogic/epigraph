// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlData extends PsiElement {

  @NotNull
  List<EdlDataEntry> getDataEntryList();

  @Nullable
  EdlTypeRef getTypeRef();

  @NotNull
  PsiElement getAngleLeft();

  @Nullable
  PsiElement getAngleRight();

}
