// This is a generated file. Not intended for manual editing.
package ws.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlListDatum extends UrlDatum {

  @NotNull
  List<UrlDataValue> getDataValueList();

  @Nullable
  UrlTypeRef getTypeRef();

  @NotNull
  PsiElement getBracketLeft();

  @Nullable
  PsiElement getBracketRight();

}
