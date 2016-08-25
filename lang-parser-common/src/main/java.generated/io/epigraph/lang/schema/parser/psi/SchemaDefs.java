// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDefs extends PsiElement {

  @NotNull
  List<SchemaSupplementDef> getSupplementDefList();

  @NotNull
  List<SchemaTypeDefWrapper> getTypeDefWrapperList();

}
