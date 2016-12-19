// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlResourceDef extends PsiElement {

  @NotNull
  List<EdlOperationDef> getOperationDefList();

  @Nullable
  EdlResourceName getResourceName();

  @Nullable
  EdlResourceType getResourceType();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getResource();

}
