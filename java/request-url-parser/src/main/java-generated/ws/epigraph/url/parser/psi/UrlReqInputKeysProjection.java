// This is a generated file. Not intended for manual editing.
package ws.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlReqInputKeysProjection extends PsiElement {

  @NotNull
  List<UrlReqInputKeyProjection> getReqInputKeyProjectionList();

  @NotNull
  PsiElement getBracketLeft();

  @Nullable
  PsiElement getBracketRight();

  @Nullable
  PsiElement getStar();

}
