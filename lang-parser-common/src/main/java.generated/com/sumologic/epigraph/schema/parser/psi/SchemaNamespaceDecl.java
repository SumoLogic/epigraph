// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaNamespaceDeclStub;
import com.sumologic.epigraph.schema.parser.Fqn;

public interface SchemaNamespaceDecl extends PsiElement, StubBasedPsiElement<SchemaNamespaceDeclStub> {

  @NotNull
  List<SchemaCustomParam> getCustomParamList();

  @Nullable
  SchemaFqn getFqn();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getNamespace();

  @Nullable
  Fqn getFqn2();

  @NotNull
  String toString();

}
