// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpOutputMultiTagProjection extends PsiElement {

  @NotNull
  List<SchemaOpOutputMultiTagProjectionItem> getOpOutputMultiTagProjectionItemList();

  @NotNull
  PsiElement getColon();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
