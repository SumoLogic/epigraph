// This is a generated file. Not intended for manual editing.
package ws.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlCreateUrl extends UrlUrl {

  @NotNull
  UrlQid getQid();

  @NotNull
  UrlReqFieldPath getReqFieldPath();

  @Nullable
  UrlReqOutputTrunkFieldProjection getReqOutputTrunkFieldProjection();

  @NotNull
  List<UrlRequestParam> getRequestParamList();

  @Nullable
  PsiElement getAngleRight();

  @NotNull
  PsiElement getSlash();

}
