// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlDefs extends PsiElement {

  @NotNull
  List<EdlResourceDef> getResourceDefList();

  @NotNull
  List<EdlSupplementDef> getSupplementDefList();

  @NotNull
  List<EdlTypeDefWrapper> getTypeDefWrapperList();

}
