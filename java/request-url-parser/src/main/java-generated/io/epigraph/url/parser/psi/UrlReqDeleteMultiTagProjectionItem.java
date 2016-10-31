// This is a generated file. Not intended for manual editing.
package io.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlReqDeleteMultiTagProjectionItem extends PsiElement {

  @NotNull
  List<UrlReqAnnotation> getReqAnnotationList();

  @Nullable
  UrlReqDeleteModelMeta getReqDeleteModelMeta();

  @NotNull
  UrlReqDeleteModelProjection getReqDeleteModelProjection();

  @NotNull
  List<UrlReqParam> getReqParamList();

  @NotNull
  UrlTagName getTagName();

  @Nullable
  PsiElement getPlus();

}
