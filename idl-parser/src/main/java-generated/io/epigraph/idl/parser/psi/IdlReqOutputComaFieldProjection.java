// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlReqOutputComaFieldProjection extends PsiElement {

  @NotNull
  IdlQid getQid();

  @NotNull
  List<IdlReqAnnotation> getReqAnnotationList();

  @NotNull
  IdlReqOutputComaVarProjection getReqOutputComaVarProjection();

  @NotNull
  List<IdlReqParam> getReqParamList();

  @Nullable
  PsiElement getPlus();

}
