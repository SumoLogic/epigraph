// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaMapDatumEntry extends PsiElement {

  @Nullable
  SchemaDataValue getDataValue();

  @NotNull
  SchemaDatum getDatum();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getComma();

}
