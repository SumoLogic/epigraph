// This is a generated file. Not intended for manual editing.
package io.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlReqOutputTrunkMapModelProjection extends PsiElement {

  @NotNull
  UrlDatum getDatum();

  @NotNull
  List<UrlReqAnnotation> getReqAnnotationList();

  @Nullable
  UrlReqOutputTrunkVarProjection getReqOutputTrunkVarProjection();

  @NotNull
  List<UrlReqParam> getReqParamList();

  @Nullable
  PsiElement getPlus();

  @NotNull
  PsiElement getSlash();

}
