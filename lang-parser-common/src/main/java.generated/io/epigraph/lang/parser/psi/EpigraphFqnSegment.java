// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import io.epigraph.lang.parser.Fqn;

public interface EpigraphFqnSegment extends PsiNameIdentifierOwner {

  @NotNull
  EpigraphQid getQid();

  @Nullable
  String getName();

  @NotNull
  PsiElement setName(String name);

  @NotNull
  PsiElement getNameIdentifier();

  @Nullable
  EpigraphFqn getSchemaFqn();

  @Nullable
  EpigraphFqnTypeRef getSchemaFqnTypeRef();

  boolean isLast();

  @Nullable
  PsiReference getReference();

  @NotNull
  Fqn getFqn();

}
