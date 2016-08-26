// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EpigraphOpOutputModelProjection extends PsiElement {

  @Nullable
  EpigraphOpOutputEnumModelProjection getOpOutputEnumModelProjection();

  @Nullable
  EpigraphOpOutputListModelProjection getOpOutputListModelProjection();

  @Nullable
  EpigraphOpOutputMapModelProjection getOpOutputMapModelProjection();

  @NotNull
  EpigraphOpOutputModelProjectionBody getOpOutputModelProjectionBody();

  @Nullable
  EpigraphOpOutputPrimitiveModelProjection getOpOutputPrimitiveModelProjection();

  @Nullable
  EpigraphOpOutputRecordModelProjection getOpOutputRecordModelProjection();

}
