// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaCustomOperationDef extends PsiElement {

  @NotNull
  List<SchemaCustomOperationBodyPart> getCustomOperationBodyPartList();

  @NotNull
  SchemaOperationName getOperationName();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getCustom();

}
