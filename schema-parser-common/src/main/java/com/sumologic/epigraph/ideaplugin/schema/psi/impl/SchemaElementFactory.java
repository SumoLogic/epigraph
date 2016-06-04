package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.sumologic.epigraph.ideaplugin.schema.SchemaLanguage;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFile;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqn;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnSegment;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaElementFactory {
  @NotNull
  public static SchemaFile createFileFromText(@NotNull Project project, @NotNull String text) {
    return (SchemaFile) PsiFileFactory.getInstance(project).createFileFromText("a.s", SchemaLanguage.INSTANCE, text);
  }

  @NotNull
  public static PsiElement createId(Project project, String text) {
    final SchemaFile file = createFileFromText(project, "namespace " + text);
    //noinspection ConstantConditions
    return ((SchemaFqnSegment) file.getNamespaceDecl().getFqn().getLastChild()).getId();
  }

  public static SchemaFqn createFqn(Project project, String text) {
    final SchemaFile file = createFileFromText(project, "namespace " + text);
    //noinspection ConstantConditions
    return file.getNamespaceDecl().getFqn();
  }
}
