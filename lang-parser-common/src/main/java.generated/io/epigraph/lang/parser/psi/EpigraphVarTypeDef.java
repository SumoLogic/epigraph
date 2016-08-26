// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.lang.parser.psi.stubs.SchemaVarTypeDefStub;

public interface EpigraphVarTypeDef extends EpigraphTypeDef, StubBasedPsiElement<SchemaVarTypeDefStub> {

  @Nullable
  SchemaDefaultOverride getDefaultOverride();

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaQid getQid();

  @Nullable
  SchemaSupplementsDecl getSupplementsDecl();

  @Nullable
  SchemaVarTypeBody getVarTypeBody();

  @Nullable
  PsiElement getAbstract();

  @NotNull
  PsiElement getVartype();

  @NotNull
  List<EpigraphTypeDef> supplemented();

}
