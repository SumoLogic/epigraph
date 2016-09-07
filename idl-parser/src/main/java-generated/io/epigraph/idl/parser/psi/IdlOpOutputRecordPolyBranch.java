// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpOutputRecordPolyBranch extends PsiElement {

  @Nullable
  IdlFqnTypeRef getFqnTypeRef();

  @Nullable
  IdlOpOutputModelProjectionBody getOpOutputModelProjectionBody();

  @Nullable
  IdlOpOutputRecordModelProjection getOpOutputRecordModelProjection();

  @Nullable
  PsiElement getAngleLeft();

  @Nullable
  PsiElement getAngleRight();

  @NotNull
  PsiElement getTilda();

}
