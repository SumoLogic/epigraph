// This is a generated file. Not intended for manual editing.
package io.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlDeleteUrl extends PsiElement {

  @NotNull
  UrlQid getQid();

  @Nullable
  UrlReqDeleteVarProjection getReqDeleteVarProjection();

  @Nullable
  UrlReqOutputTrunkFieldProjection getReqOutputTrunkFieldProjection();

  @NotNull
  UrlReqVarPath getReqVarPath();

  @NotNull
  List<UrlRequestParam> getRequestParamList();

  @Nullable
  PsiElement getAngleLeft();

  @Nullable
  PsiElement getAngleRight();

  @NotNull
  PsiElement getSlash();

}
