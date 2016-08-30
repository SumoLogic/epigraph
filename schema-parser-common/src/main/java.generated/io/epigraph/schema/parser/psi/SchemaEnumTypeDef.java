// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.schema.parser.psi.stubs.SchemaEnumTypeDefStub;

public interface SchemaEnumTypeDef extends SchemaTypeDef, StubBasedPsiElement<SchemaEnumTypeDefStub> {

  @Nullable
  SchemaEnumTypeBody getEnumTypeBody();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  SchemaQid getQid();

  @NotNull
  PsiElement getEnum();

}
