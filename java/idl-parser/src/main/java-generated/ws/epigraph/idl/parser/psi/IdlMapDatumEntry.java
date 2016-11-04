// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlMapDatumEntry extends PsiElement {

  @Nullable
  IdlDataValue getDataValue();

  @NotNull
  IdlDatum getDatum();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getComma();

}
