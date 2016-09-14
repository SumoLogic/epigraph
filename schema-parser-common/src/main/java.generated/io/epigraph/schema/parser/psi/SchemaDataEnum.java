// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDataEnum extends SchemaDataValue {

  @Nullable
  SchemaFqnTypeRef getFqnTypeRef();

  @NotNull
  SchemaQid getQid();

  @Nullable
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
