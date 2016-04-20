// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaEnumTypeBody extends PsiElement {

  @NotNull
  List<SchemaCustomParam> getCustomParamList();

  @NotNull
  List<SchemaEnumMemberDecl> getEnumMemberDeclList();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
