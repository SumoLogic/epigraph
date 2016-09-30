// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public interface SchemaAnnotation extends PsiNamedElement {

  @Nullable
  SchemaDataValue getDataValue();

  @NotNull
  SchemaQid getQid();

  @NotNull
  PsiElement getEq();

  @Nullable
  String getName();

  PsiElement setName(String name);

  @NotNull
  PsiElement getNameIdentifier();

}
