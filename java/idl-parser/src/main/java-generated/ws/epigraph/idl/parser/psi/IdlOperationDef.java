// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOperationDef extends PsiElement {

  @Nullable
  IdlCreateOperationDef getCreateOperationDef();

  @Nullable
  IdlCustomOperationDef getCustomOperationDef();

  @Nullable
  IdlDeleteOperationDef getDeleteOperationDef();

  @Nullable
  IdlReadOperationDef getReadOperationDef();

  @Nullable
  IdlUpdateOperationDef getUpdateOperationDef();

}
