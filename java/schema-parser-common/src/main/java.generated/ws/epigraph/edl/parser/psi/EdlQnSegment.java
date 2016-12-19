// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import ws.epigraph.lang.Qn;

public interface EdlQnSegment extends PsiNameIdentifierOwner {

  @NotNull
  EdlQid getQid();

  @Nullable
  String getName();

  @NotNull
  PsiElement setName(String name);

  @NotNull
  PsiElement getNameIdentifier();

  @Nullable
  EdlQn getEdlFqn();

  @Nullable
  EdlQnTypeRef getEdlFqnTypeRef();

  boolean isLast();

  @Nullable
  PsiReference getReference();

  @NotNull
  Qn getQn();

}
