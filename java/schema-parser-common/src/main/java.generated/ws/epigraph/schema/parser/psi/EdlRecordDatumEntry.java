// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlRecordDatumEntry extends PsiElement {

  @Nullable
  EdlDataValue getDataValue();

  @NotNull
  EdlQid getQid();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getComma();

}
