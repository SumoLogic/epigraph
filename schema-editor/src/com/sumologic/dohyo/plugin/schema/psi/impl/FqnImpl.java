package com.sumologic.dohyo.plugin.schema.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.sumologic.dohyo.plugin.schema.psi.Fqn;
import org.jetbrains.annotations.NotNull;

import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class FqnImpl extends ASTWrapperPsiElement implements Fqn {
  public FqnImpl(@NotNull ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public List<PsiElement> getSegments() {
    List<PsiElement> res = new ArrayList<>();
    for (PsiElement child = getFirstChild(); child != null; child = child.getNextSibling()) {
      if (child.getNode().getElementType() == S_ID) {
        res.add(child);
      }
    }
    return res;
  }

  @NotNull
  @Override
  public String getFqnString() {
    StringBuilder res = new StringBuilder();

    for (PsiElement psiElement : getSegments()) {
      if (res.length() > 0) res.append('.');
      res.append(psiElement.getText().trim());
    }

    return res.toString();
  }
}
