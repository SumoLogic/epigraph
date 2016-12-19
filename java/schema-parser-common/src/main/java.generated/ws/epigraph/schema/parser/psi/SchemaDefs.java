// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDefs extends PsiElement {

  @NotNull
  List<SchemaResourceDef> getResourceDefList();

  @NotNull
  List<SchemaSupplementDef> getSupplementDefList();

  @NotNull
  List<SchemaTypeDefWrapper> getTypeDefWrapperList();

}