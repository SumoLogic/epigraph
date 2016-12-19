// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOperationDef extends PsiElement {

  @Nullable
  SchemaCreateOperationDef getCreateOperationDef();

  @Nullable
  SchemaCustomOperationDef getCustomOperationDef();

  @Nullable
  SchemaDeleteOperationDef getDeleteOperationDef();

  @Nullable
  SchemaReadOperationDef getReadOperationDef();

  @Nullable
  SchemaUpdateOperationDef getUpdateOperationDef();

}
