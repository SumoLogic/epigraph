package com.sumologic.epigraph.schema.parser.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.sumologic.epigraph.schema.parser.psi.*;
import com.sumologic.epigraph.schema.parser.SchemaLanguage;
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
    return ((SchemaFqnSegment) file.getNamespaceDecl().getFqn().getLastChild()).getQid().getId();
  }

  @NotNull
  public static PsiElement createBackTick(Project project) {
    final SchemaFile file = createFileFromText(project, "namespace some\n long `LL`");
    //noinspection ConstantConditions
    return file.getDefs().getTypeDefWrapperList().get(0).getPrimitiveTypeDef().getQid().getFirstChild();
  }

  public static SchemaFqn createFqn(Project project, String text) {
    final SchemaFile file = createFileFromText(project, "namespace " + text);
    //noinspection ConstantConditions
    return file.getNamespaceDecl().getFqn();
  }

  public static SchemaImports createImports(Project project, String importToAdd) {
    final SchemaFile file = createFileFromText(project, "namespace some\n import " + importToAdd);
    return file.getImportsStatement();
  }

  public static SchemaImportStatement createImport(Project project, String importToAdd) {
    final SchemaFile file = createFileFromText(project, "namespace some\n import " + importToAdd);
    return file.getImportStatements().get(0);
  }

  public static PsiElement createWhitespaces(Project project, String text) {
    final SchemaFile file = createFileFromText(project, text+"namespace some");
    return file.getChildren()[0];
  }
}
