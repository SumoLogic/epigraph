// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpOutputModelProjection extends PsiElement {

  @Nullable
  SchemaOpOutputEnumModelProjection getOpOutputEnumModelProjection();

  @Nullable
  SchemaOpOutputListModelProjection getOpOutputListModelProjection();

  @Nullable
  SchemaOpOutputMapModelProjection getOpOutputMapModelProjection();

  @NotNull
  SchemaOpOutputModelProjectionBody getOpOutputModelProjectionBody();

  @Nullable
  SchemaOpOutputPrimitiveModelProjection getOpOutputPrimitiveModelProjection();

  @Nullable
  SchemaOpOutputRecordModelProjection getOpOutputRecordModelProjection();

}
