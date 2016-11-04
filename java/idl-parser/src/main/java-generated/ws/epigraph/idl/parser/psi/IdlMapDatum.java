// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlMapDatum extends IdlDatum {

  @NotNull
  List<IdlMapDatumEntry> getMapDatumEntryList();

  @Nullable
  IdlTypeRef getTypeRef();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
