// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EpigraphDefs extends PsiElement {

  @NotNull
  List<EpigraphSupplementDef> getSupplementDefList();

  @NotNull
  List<EpigraphTypeDefWrapper> getTypeDefWrapperList();

}
