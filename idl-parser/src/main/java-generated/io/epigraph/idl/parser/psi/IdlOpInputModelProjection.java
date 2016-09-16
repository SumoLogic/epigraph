// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpInputModelProjection extends PsiElement {

  @Nullable
  IdlOpInputListModelProjection getOpInputListModelProjection();

  @Nullable
  IdlOpInputMapModelProjection getOpInputMapModelProjection();

  @Nullable
  IdlOpInputModelProjectionBody getOpInputModelProjectionBody();

  @Nullable
  IdlOpInputRecordModelProjection getOpInputRecordModelProjection();

}
