// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EpigraphDataVarEntry extends PsiElement {

  @Nullable
  EpigraphDataPrimitive getDataPrimitive();

  @Nullable
  EpigraphDataValue getDataValue();

  @NotNull
  List<EpigraphFqnTypeRef> getFqnTypeRefList();

  @NotNull
  EpigraphQid getQid();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getComma();

  @Nullable
  PsiElement getNull();

}
