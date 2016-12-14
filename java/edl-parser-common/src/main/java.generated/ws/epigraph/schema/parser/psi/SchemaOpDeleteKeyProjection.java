// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpDeleteKeyProjection extends PsiElement {

  @NotNull
  List<SchemaOpDeleteKeyProjectionPart> getOpDeleteKeyProjectionPartList();

  @NotNull
  PsiElement getBracketLeft();

  @Nullable
  PsiElement getBracketRight();

  @Nullable
  PsiElement getForbidden();

  @Nullable
  PsiElement getRequired();

}
