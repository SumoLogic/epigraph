// This is a generated file. Not intended for manual editing.
package ws.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlReqMapModelPath extends PsiElement {

  @NotNull
  UrlDatum getDatum();

  @NotNull
  List<UrlReqAnnotation> getReqAnnotationList();

  @NotNull
  List<UrlReqParam> getReqParamList();

  @Nullable
  UrlReqVarPath getReqVarPath();

  @NotNull
  PsiElement getSlash();

}