// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpParameters extends PsiElement {

  @NotNull
  List<IdlOpParamProjection> getOpParamProjectionList();

  @NotNull
  PsiElement getColon();

  @NotNull
  PsiElement getCurlyLeft();

  @NotNull
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getParameters();

}
