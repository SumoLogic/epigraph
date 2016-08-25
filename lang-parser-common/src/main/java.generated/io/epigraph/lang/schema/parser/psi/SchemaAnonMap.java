// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaAnonMap extends SchemaTypeRef {

  @Nullable
  SchemaTypeRef getTypeRef();

  @Nullable
  SchemaValueTypeRef getValueTypeRef();

  @Nullable
  PsiElement getBracketLeft();

  @Nullable
  PsiElement getBracketRight();

  @Nullable
  PsiElement getComma();

  @NotNull
  PsiElement getMap();

}
