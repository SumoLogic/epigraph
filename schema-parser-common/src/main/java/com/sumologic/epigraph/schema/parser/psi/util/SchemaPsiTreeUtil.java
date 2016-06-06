package com.sumologic.epigraph.schema.parser.psi.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPsiTreeUtil extends PsiTreeUtil {
  // TODO remove once IDEA 2016.1.3 is out

  @Nullable
  public static <T extends PsiElement> T getStubChildOfType(@Nullable PsiElement element, @NotNull Class<T> aClass) {
    if (element == null) return null;
    StubElement<?> stub = element instanceof StubBasedPsiElement ? ((StubBasedPsiElement) element).getStub() : null;
    if (stub == null) {
      return getChildOfType(element, aClass);
    }
    for (StubElement childStub : stub.getChildrenStubs()) {
      PsiElement child = childStub.getPsi();
      if (aClass.isInstance(child)) {
        //noinspection unchecked
        return (T) child;
      }
    }
    return null;
  }
}
