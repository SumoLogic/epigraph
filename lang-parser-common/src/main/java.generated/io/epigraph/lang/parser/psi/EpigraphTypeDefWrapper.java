// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.lang.parser.psi.stubs.EpigraphTypeDefWrapperStub;

public interface EpigraphTypeDefWrapper extends PsiElement, StubBasedPsiElement<EpigraphTypeDefWrapperStub> {

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
