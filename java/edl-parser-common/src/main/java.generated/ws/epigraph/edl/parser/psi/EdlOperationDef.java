// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOperationDef extends PsiElement {

  @Nullable
  EdlCreateOperationDef getCreateOperationDef();

  @Nullable
  EdlCustomOperationDef getCustomOperationDef();

  @Nullable
  EdlDeleteOperationDef getDeleteOperationDef();

  @Nullable
  EdlReadOperationDef getReadOperationDef();

  @Nullable
  EdlUpdateOperationDef getUpdateOperationDef();

}