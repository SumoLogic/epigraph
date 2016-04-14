// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaAnonMap extends PsiElement {

  @Nullable
  SchemaDefaultOverride getDefaultOverride();

  @NotNull
  List<SchemaTypeRef> getTypeRefList();

  @NotNull
  PsiElement getBracketLeft();

  @NotNull
  PsiElement getBracketRight();

  @NotNull
  PsiElement getComma();

  @NotNull
  PsiElement getMap();

}
