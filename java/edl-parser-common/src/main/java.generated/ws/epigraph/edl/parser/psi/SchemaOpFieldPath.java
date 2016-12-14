// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpFieldPath extends PsiElement {

  @NotNull
  List<SchemaOpFieldPathBodyPart> getOpFieldPathBodyPartList();

  @Nullable
  SchemaOpVarPath getOpVarPath();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
