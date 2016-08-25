// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaEnumTypeBody extends CustomParamsHolder {

  @NotNull
  List<SchemaCustomParam> getCustomParamList();

  @NotNull
  List<SchemaEnumMemberDecl> getEnumMemberDeclList();

  @NotNull
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
