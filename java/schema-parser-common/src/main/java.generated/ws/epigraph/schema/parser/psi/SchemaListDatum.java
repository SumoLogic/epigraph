// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaListDatum extends SchemaDatum {

  @NotNull
  List<SchemaDataValue> getDataValueList();

  @Nullable
  SchemaTypeRef getTypeRef();

  @NotNull
  PsiElement getBracketLeft();

  @Nullable
  PsiElement getBracketRight();

}
