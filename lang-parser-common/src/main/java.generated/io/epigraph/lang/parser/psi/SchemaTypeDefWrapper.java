// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import io.epigraph.lang.parser.psi.stubs.EpigraphTypeDefWrapperStub;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;

public interface SchemaTypeDefWrapper extends PsiElement, StubBasedPsiElement<EpigraphTypeDefWrapperStub> {

  @Nullable
  EpigraphEnumTypeDef getEnumTypeDef();

  @Nullable
  EpigraphListTypeDef getListTypeDef();

  @Nullable
  EpigraphMapTypeDef getMapTypeDef();

  @Nullable
  EpigraphPrimitiveTypeDef getPrimitiveTypeDef();

  @Nullable
  EpigraphRecordTypeDef getRecordTypeDef();

  @Nullable
  EpigraphVarTypeDef getVarTypeDef();

  @NotNull
  EpigraphTypeDef getElement();

  void delete();

  @NotNull
  String toString();

}
