package com.sumologic.dohyo.plugin.schema.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface Fqn extends PsiElement {
  @NotNull
  List<PsiElement> getSegments();

  @NotNull
  String getFqnString();
}
