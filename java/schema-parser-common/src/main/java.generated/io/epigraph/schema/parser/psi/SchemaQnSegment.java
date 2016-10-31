// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import io.epigraph.lang.Qn;

public interface SchemaQnSegment extends PsiNameIdentifierOwner {

  @NotNull
  SchemaQid getQid();

  @Nullable
  String getName();

  @NotNull
  PsiElement setName(String name);

  @NotNull
  PsiElement getNameIdentifier();

  @Nullable
  SchemaQn getSchemaFqn();

  @Nullable
  SchemaQnTypeRef getSchemaFqnTypeRef();

  boolean isLast();

  @Nullable
  PsiReference getReference();

  @NotNull
  Qn getQn();

}
