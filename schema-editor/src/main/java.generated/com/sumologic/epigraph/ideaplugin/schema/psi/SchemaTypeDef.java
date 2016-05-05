// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaTypeDef extends TypeDefSchemaElement {

  @Nullable
  String getName();

  @Nullable
  PsiElement setName(String name);

  @Nullable
  PsiElement getNameIdentifier();

  int getTextOffset();

  void delete();

}
