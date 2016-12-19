// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;

public interface EdlVarTagRef extends PsiNameIdentifierOwner {

  @NotNull
  EdlQid getQid();

  PsiElement setName(String name);

  @Nullable
  PsiElement getNameIdentifier();

  @Nullable
  PsiReference getReference();

}