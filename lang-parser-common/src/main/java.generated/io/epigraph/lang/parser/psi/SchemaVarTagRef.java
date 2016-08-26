// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;

public interface SchemaVarTagRef extends PsiNameIdentifierOwner {

  @NotNull
  SchemaQid getQid();

  PsiElement setName(String name);

  @Nullable
  PsiElement getNameIdentifier();

  @Nullable
  PsiReference getReference();

}
