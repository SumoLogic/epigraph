// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.schema.parser.psi.stubs.SchemaMapTypeDefStub;

public interface SchemaMapTypeDef extends SchemaTypeDef, StubBasedPsiElement<SchemaMapTypeDefStub> {

  @NotNull
  SchemaAnonMap getAnonMap();

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaMapTypeBody getMapTypeBody();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  SchemaQid getQid();

  @Nullable
  SchemaSupplementsDecl getSupplementsDecl();

  @Nullable
  PsiElement getAbstract();

}
