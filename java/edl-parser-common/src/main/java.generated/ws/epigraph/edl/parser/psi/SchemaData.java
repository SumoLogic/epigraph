// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaData extends PsiElement {

  @NotNull
  List<SchemaDataEntry> getDataEntryList();

  @Nullable
  SchemaTypeRef getTypeRef();

  @NotNull
  PsiElement getAngleLeft();

  @Nullable
  PsiElement getAngleRight();

}
