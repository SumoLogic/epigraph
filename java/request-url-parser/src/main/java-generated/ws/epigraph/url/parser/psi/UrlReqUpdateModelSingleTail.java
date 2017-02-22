// This is a generated file. Not intended for manual editing.
package ws.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlReqUpdateModelSingleTail extends PsiElement {

  @NotNull
  List<UrlReqAnnotation> getReqAnnotationList();

  @NotNull
  List<UrlReqParam> getReqParamList();

  @NotNull
  UrlReqUpdateModelProjection getReqUpdateModelProjection();

  @NotNull
  UrlTypeRef getTypeRef();

  @Nullable
  PsiElement getPlus();

}
