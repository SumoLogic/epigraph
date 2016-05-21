// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaTypeDefStub;
import javax.swing.Icon;

public interface SchemaTypeDef extends SchemaTypeDefElement, StubBasedPsiElement<SchemaTypeDefStub> {

  @Nullable
  SchemaEnumTypeDef getEnumTypeDef();

  @Nullable
  SchemaListTypeDef getListTypeDef();

  @Nullable
  SchemaMapTypeDef getMapTypeDef();

  @Nullable
  SchemaPrimitiveTypeDef getPrimitiveTypeDef();

  @Nullable
  SchemaRecordTypeDef getRecordTypeDef();

  @Nullable
  SchemaVarTypeDef getVarTypeDef();

  @NotNull
  SchemaTypeDefElement element();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  PsiElement getAbstract();

  @Nullable
  PsiElement getPolymorphic();

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  PsiElement getId();

  @NotNull
  String toString();

  @Nullable
  String getName();

  @Nullable
  PsiElement setName(String name);

  @Nullable
  PsiElement getNameIdentifier();

  int getTextOffset();

  void delete();

  @NotNull
  TypeKind getKind();

  Icon getIcon(int flags);

  @NotNull
  List<SchemaTypeDef> parents();

}
