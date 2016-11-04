// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlAnnotation extends PsiElement {

  @Nullable
  IdlDataValue getDataValue();

  @NotNull
  IdlQid getQid();

  @NotNull
  PsiElement getEq();

}
