package com.sumologic.dohyo.plugin.schema.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.sumologic.dohyo.plugin.schema.SchemaLanguage;
import com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFile;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFqn;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaElementFactory {
  @NotNull
  public static SchemaFile createFileFromText(@NotNull Project project, @NotNull String text) {
    return (SchemaFile) PsiFileFactory.getInstance(project).createFileFromText("a.s", SchemaLanguage.INSTANCE, text);
  }

  public static PsiElement createId(String text) {
    // TODO validate this is correct
    return new LeafPsiElement(SchemaElementTypes.S_ID, text).getNextSibling();
  }

  public static SchemaFqn createFqn(Project project, String text) {
    final SchemaFile file = createFileFromText(project, "namespace " + text);
    //noinspection ConstantConditions
    return file.getNamespaceDecl().getFqn();
  }
}
