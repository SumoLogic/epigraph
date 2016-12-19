// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDeleteOperationBodyPart extends PsiElement {

  @Nullable
  SchemaAnnotation getAnnotation();

  @Nullable
  SchemaOperationDeleteProjection getOperationDeleteProjection();

  @Nullable
  SchemaOperationOutputProjection getOperationOutputProjection();

  @Nullable
  SchemaOperationOutputType getOperationOutputType();

  @Nullable
  SchemaOperationPath getOperationPath();

}
