// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpInputMapModelProjection extends IdlOpInputModelProjection {

  @NotNull
  IdlOpInputKeyProjection getOpInputKeyProjection();

  @Nullable
  IdlOpInputVarProjection getOpInputVarProjection();

  @Nullable
  PsiElement getStar();

}