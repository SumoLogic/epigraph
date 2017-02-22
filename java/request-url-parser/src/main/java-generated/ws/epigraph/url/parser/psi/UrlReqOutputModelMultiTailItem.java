// This is a generated file. Not intended for manual editing.
package ws.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlReqOutputModelMultiTailItem extends PsiElement {

  @NotNull
  List<UrlReqAnnotation> getReqAnnotationList();

  @NotNull
  UrlReqOutputComaModelProjection getReqOutputComaModelProjection();

  @Nullable
  UrlReqOutputModelMeta getReqOutputModelMeta();

  @NotNull
  List<UrlReqParam> getReqParamList();

  @NotNull
  UrlTypeRef getTypeRef();

  @Nullable
  PsiElement getPlus();

}
