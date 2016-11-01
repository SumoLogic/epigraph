// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlCustomOperationBodyPart extends PsiElement {

  @Nullable
  IdlAnnotation getAnnotation();

  @Nullable
  IdlOpParam getOpParam();

  @Nullable
  IdlOperationInputProjection getOperationInputProjection();

  @Nullable
  IdlOperationInputType getOperationInputType();

  @Nullable
  IdlOperationMethod getOperationMethod();

  @Nullable
  IdlOperationOutputProjection getOperationOutputProjection();

  @Nullable
  IdlOperationOutputType getOperationOutputType();

  @Nullable
  IdlOperationPath getOperationPath();

}
