// This is a generated file. Not intended for manual editing.
package io.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlData extends PsiElement {

  @NotNull
  List<UrlDataEntry> getDataEntryList();

  @Nullable
  UrlTypeRef getTypeRef();

  @NotNull
  PsiElement getAngleLeft();

  @Nullable
  PsiElement getAngleRight();

}
