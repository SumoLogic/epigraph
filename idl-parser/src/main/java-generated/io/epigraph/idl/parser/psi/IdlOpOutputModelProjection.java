// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpOutputModelProjection extends PsiElement {

  @Nullable
  IdlOpOutputListModelProjection getOpOutputListModelProjection();

  @Nullable
  IdlOpOutputMapModelProjection getOpOutputMapModelProjection();

  @Nullable
  IdlOpOutputModelProjectionBody getOpOutputModelProjectionBody();

  @Nullable
  IdlOpOutputRecordModelProjection getOpOutputRecordModelProjection();

}
