// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public interface EdlAnnotation extends PsiNamedElement {

  @Nullable
  EdlDataValue getDataValue();

  @NotNull
  EdlQid getQid();

  @NotNull
  PsiElement getEq();

  @Nullable
  String getName();

  PsiElement setName(String name);

  @NotNull
  PsiElement getNameIdentifier();

}
