// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOpVarPath extends PsiElement {

  @NotNull
  EdlOpModelPath getOpModelPath();

  @NotNull
  List<EdlOpModelPathProperty> getOpModelPathPropertyList();

  @Nullable
  EdlTagName getTagName();

  @Nullable
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
