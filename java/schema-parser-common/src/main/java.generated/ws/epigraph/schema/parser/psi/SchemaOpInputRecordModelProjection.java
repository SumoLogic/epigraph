// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpInputRecordModelProjection extends PsiElement {

  @NotNull
  List<SchemaOpInputFieldProjectionEntry> getOpInputFieldProjectionEntryList();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
