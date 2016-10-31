// This is a generated file. Not intended for manual editing.
package io.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlReqUpdateFieldProjection extends PsiElement {

  @NotNull
  UrlQid getQid();

  @NotNull
  List<UrlReqAnnotation> getReqAnnotationList();

  @NotNull
  List<UrlReqParam> getReqParamList();

  @NotNull
  UrlReqUpdateVarProjection getReqUpdateVarProjection();

}
