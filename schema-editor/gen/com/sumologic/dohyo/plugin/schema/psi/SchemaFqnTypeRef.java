// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public interface SchemaFqnTypeRef extends PsiNamedElement {

  @NotNull
  SchemaFqn getFqn();

  String getName();

  PsiElement setName(String name);

}
