// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaReadOperationDef extends PsiElement {

  @Nullable
  SchemaOperationName getOperationName();

  @NotNull
  List<SchemaReadOperationBodyPart> getReadOperationBodyPartList();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getRead();

}
