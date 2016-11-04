// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlListDatum extends IdlDatum {

  @NotNull
  List<IdlDataValue> getDataValueList();

  @Nullable
  IdlTypeRef getTypeRef();

  @NotNull
  PsiElement getBracketLeft();

  @Nullable
  PsiElement getBracketRight();

}
