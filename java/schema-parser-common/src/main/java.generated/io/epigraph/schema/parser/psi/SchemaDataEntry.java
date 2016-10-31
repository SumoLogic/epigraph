// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDataEntry extends PsiElement {

  @Nullable
  SchemaDatum getDatum();

  @NotNull
  SchemaQid getQid();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getComma();

}
