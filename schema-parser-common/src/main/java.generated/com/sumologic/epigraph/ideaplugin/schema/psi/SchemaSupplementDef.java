// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaSupplementDefStub;
import com.intellij.navigation.ItemPresentation;

public interface SchemaSupplementDef extends PsiElement, StubBasedPsiElement<SchemaSupplementDefStub> {

  @NotNull
  List<SchemaFqnTypeRef> getFqnTypeRefList();

  @NotNull
  PsiElement getSupplement();

  @Nullable
  PsiElement getWith();

  @Nullable
  SchemaFqnTypeRef sourceRef();

  @NotNull
  List<SchemaFqnTypeRef> supplementedRefs();

  @Nullable
  SchemaTypeDef source();

  @NotNull
  List<SchemaTypeDef> supplemented();

  @NotNull
  ItemPresentation getPresentation();

  @NotNull
  String toString();

}
