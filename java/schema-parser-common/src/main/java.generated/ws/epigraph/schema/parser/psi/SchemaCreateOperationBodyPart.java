// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaCreateOperationBodyPart extends PsiElement {

  @Nullable
  SchemaAnnotation getAnnotation();

  @Nullable
  SchemaOperationInputProjection getOperationInputProjection();

  @Nullable
  SchemaOperationInputType getOperationInputType();

  @Nullable
  SchemaOperationOutputProjection getOperationOutputProjection();

  @Nullable
  SchemaOperationOutputType getOperationOutputType();

  @Nullable
  SchemaOperationPath getOperationPath();

}
