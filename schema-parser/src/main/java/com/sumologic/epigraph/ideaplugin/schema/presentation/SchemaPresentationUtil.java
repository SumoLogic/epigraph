package com.sumologic.epigraph.ideaplugin.schema.presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPresentationUtil {
  public static final Icon SCHEMA_FILE_ICON = null;

  @NotNull
  public static ItemPresentation getPresentation(@NotNull PsiElement element, boolean structureView) {
    return new StaticItemPresentation(null,null,null);
  }

  @NotNull
  public static String psiToString(@NotNull PsiElement element) {
    return element.getClass().getSimpleName() + "(" + element.getNode().getElementType().toString() + ")";
  }
}
