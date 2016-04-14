// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaTypeRef extends PsiElement {

  @Nullable
  SchemaAnonList getAnonList();

  @Nullable
  SchemaAnonMap getAnonMap();

  @Nullable
  SchemaFqn getFqn();

  @Nullable
  PsiElement getId();

}
