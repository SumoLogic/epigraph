// This is a generated file. Not intended for manual editing.
package io.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlMapDatum extends UrlDatum {

  @NotNull
  List<UrlMapDatumEntry> getMapDatumEntryList();

  @Nullable
  UrlTypeRef getTypeRef();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
