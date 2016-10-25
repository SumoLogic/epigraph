// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpInputMapModelProjection extends PsiElement {

  @NotNull
  IdlOpInputKeyProjection getOpInputKeyProjection();

  @NotNull
  IdlOpInputVarProjection getOpInputVarProjection();

  @NotNull
  PsiElement getParenLeft();

  @NotNull
  PsiElement getParenRight();

}
