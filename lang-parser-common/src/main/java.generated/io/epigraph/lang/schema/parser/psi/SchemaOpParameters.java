// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpParameters extends PsiElement {

  @NotNull
  List<SchemaOpParamProjection> getOpParamProjectionList();

  @NotNull
  PsiElement getColon();

  @NotNull
  PsiElement getCurlyLeft();

  @NotNull
  PsiElement getCurlyRight();

}
