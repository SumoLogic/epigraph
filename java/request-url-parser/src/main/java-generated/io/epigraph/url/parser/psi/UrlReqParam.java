// This is a generated file. Not intended for manual editing.
package io.epigraph.url.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UrlReqParam extends PsiElement {

  @NotNull
  UrlDatum getDatum();

  @NotNull
  UrlQid getQid();

  @NotNull
  PsiElement getEq();

  @NotNull
  PsiElement getSemicolon();

}
