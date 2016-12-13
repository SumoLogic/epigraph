// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpDeleteFieldProjection extends PsiElement {

  @NotNull
  List<SchemaOpDeleteFieldProjectionBodyPart> getOpDeleteFieldProjectionBodyPartList();

  @NotNull
  SchemaOpDeleteVarProjection getOpDeleteVarProjection();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
