package com.sumologic.dohyo.plugin.schema.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPsiUtil {
//  @Nullable
//  public static <T extends PsiElement> T findFirstParent(@NotNull PsiElement e, Class<T> cls) {
//    //noinspection unchecked
//    return (T) PsiTreeUtil.findFirstParent(e, cls::isInstance);
//  }
//
//  public static boolean hasParent(@NotNull PsiElement e, Class<?>... classes) {
//    return null != PsiTreeUtil.findFirstParent(e, psiElement -> {
//      for (Class<?> cls : classes) {
//        if (cls.isInstance(psiElement)) return true;
//      }
//      return false;
//    });
//  }

  public static boolean hasNextSibling(@NotNull PsiElement e, IElementType... elementTypes) {
    for (PsiElement nextSibling = e.getNextSibling(); nextSibling != null; nextSibling = nextSibling.getNextSibling()) {
      for (IElementType elementType : elementTypes) {
        if (nextSibling.getNode().getElementType().equals(elementType)) return true;
      }
    }

    return false;
  }

  public static boolean hasNextLeaf(@NotNull PsiElement e, IElementType... elementTypes) {
    PsiElement leaf = PsiTreeUtil.nextLeaf(e);
    while (leaf != null) {
      for (IElementType elementType : elementTypes) {
        if (elementType.equals(leaf.getNode().getElementType())) return true;
      }
      leaf = PsiTreeUtil.nextLeaf(leaf);
    }
    return false;
  }

  @Nullable
  public static PsiElement prevNonWhitespaceSibling(@NotNull PsiElement e) {
    PsiElement res = e.getPrevSibling();
    while (res != null && res.getNode().getElementType() == TokenType.WHITE_SPACE) {
      res = res.getPrevSibling();
    }

    return res;
  }

  @Nullable
  public static PsiElement nextNonWhitespaceSibling(@NotNull PsiElement e) {
    PsiElement res = e.getNextSibling();
    while (res != null && res.getNode().getElementType() == TokenType.WHITE_SPACE) {
      res = res.getNextSibling();
    }

    return res;
  }

  public static boolean hasChildOfType(@NotNull PsiElement e, IElementType... elementTypes) {
    PsiElement firstChild = e.getFirstChild();
    return firstChild != null && hasNextSibling(firstChild, elementTypes);
  }
}
