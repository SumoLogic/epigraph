package com.sumologic.epigraph.ideaplugin.schema.presentation;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPresentationUtil {
  @Nullable
  public static String getName(@NotNull PsiNamedElement element, boolean qualified) {
    String shortName = element.getName();
    if (qualified) {
      String namespace;
      if (element instanceof SchemaTypeDef) {
        SchemaTypeDef typeDef = (SchemaTypeDef) element;
        namespace = typeDef.getNamespace();
      } else namespace = NamespaceManager.getNamespace(element);
      return namespace == null ? shortName : namespace + '.' + shortName;
    } else return shortName;
  }

  @NotNull
  public static String psiToString(@NotNull PsiElement element) {
    return element.getClass().getSimpleName() + "(" + element.getNode().getElementType().toString() + ")";
  }
}
