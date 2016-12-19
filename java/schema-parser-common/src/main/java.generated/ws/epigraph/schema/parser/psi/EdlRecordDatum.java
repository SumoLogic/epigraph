// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlRecordDatum extends EdlDatum {

  @NotNull
  List<EdlRecordDatumEntry> getRecordDatumEntryList();

  @Nullable
  EdlTypeRef getTypeRef();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
