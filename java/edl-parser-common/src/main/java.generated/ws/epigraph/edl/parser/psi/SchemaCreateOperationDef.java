// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaCreateOperationDef extends PsiElement {

  @NotNull
  List<SchemaCreateOperationBodyPart> getCreateOperationBodyPartList();

  @Nullable
  SchemaOperationName getOperationName();

  @NotNull
  PsiElement getCreate();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
