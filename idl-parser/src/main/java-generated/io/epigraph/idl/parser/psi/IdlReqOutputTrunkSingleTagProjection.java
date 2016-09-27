// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlReqOutputTrunkSingleTagProjection extends PsiElement {

  @NotNull
  List<IdlReqAnnotation> getReqAnnotationList();

  @Nullable
  IdlReqOutputModelMeta getReqOutputModelMeta();

  @NotNull
  IdlReqOutputTrunkModelProjection getReqOutputTrunkModelProjection();

  @NotNull
  List<IdlReqParam> getReqParamList();

  @Nullable
  IdlTagName getTagName();

  @Nullable
  PsiElement getColon();

  @Nullable
  PsiElement getPlus();

}
