package com.sumologic.epigraph.ideaplugin.schema.presentation;

import com.intellij.psi.PsiNamedElement;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
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
      String namespace = NamespaceManager.getNamespace(element);
      return namespace == null ? shortName : namespace + '.' + shortName;
    } else return shortName;
  }
}
