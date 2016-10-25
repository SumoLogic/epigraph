// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpMapModelPath extends PsiElement {

  @Nullable
  IdlOpPathKeyProjection getOpPathKeyProjection();

  @NotNull
  IdlOpVarPath getOpVarPath();

  @NotNull
  PsiElement getSlash();

  @NotNull
  PsiElement getStar();

}
