// This is a generated file. Not intended for manual editing.
package io.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlReadUrl extends PsiElement {

  @NotNull
  UrlQid getQid();

  @NotNull
  UrlReqOutputTrunkFieldProjection getReqOutputTrunkFieldProjection();

  @NotNull
  UrlReqVarPath getReqVarPath();

  @NotNull
  List<UrlRequestParam> getRequestParamList();

  @NotNull
  PsiElement getSlash();

}
