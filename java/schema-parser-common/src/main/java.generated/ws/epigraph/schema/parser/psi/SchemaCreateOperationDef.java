// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaCreateOperationDef extends PsiElement {

  @NotNull
  List<SchemaCreateOperationBodyPart> getCreateOperationBodyPartList();

  @Nullable
  SchemaOperationName getOperationName();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getOpCreate();

}
