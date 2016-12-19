// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlMapDatum extends EdlDatum {

  @NotNull
  List<EdlMapDatumEntry> getMapDatumEntryList();

  @Nullable
  EdlTypeRef getTypeRef();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
