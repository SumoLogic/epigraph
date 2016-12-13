// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaResourceDef extends PsiElement {

  @NotNull
  List<SchemaOperationDef> getOperationDefList();

  @NotNull
  SchemaResourceName getResourceName();

  @NotNull
  SchemaResourceType getResourceType();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getResource();

}
