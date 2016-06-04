// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaFqnTypeRef extends PsiElement {

  @NotNull
  SchemaFqn getFqn();

  @Nullable
  SchemaTypeDef resolve();

}
