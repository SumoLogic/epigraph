// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpInputComaFieldProjection extends PsiElement {

  @Nullable
  IdlOpInputComaVarProjection getOpInputComaVarProjection();

  @NotNull
  List<IdlOpInputFieldProjectionBodyPart> getOpInputFieldProjectionBodyPartList();

  @NotNull
  IdlQid getQid();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @Nullable
  PsiElement getPlus();

}
