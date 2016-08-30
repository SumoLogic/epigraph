// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.schema.parser.psi.stubs.SchemaVarTypeDefStub;

public interface SchemaVarTypeDef extends SchemaTypeDef, StubBasedPsiElement<SchemaVarTypeDefStub> {

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
  List<SchemaTypeDef> supplemented();

}
