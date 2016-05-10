// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDefs extends PsiElement {

  @NotNull
  List<SchemaSupplementDef> getSupplementDefList();

  @NotNull
  List<SchemaTypeDef> getTypeDefList();

}
