package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.sumologic.epigraph.schema.parser.psi.SchemaFile;
import com.sumologic.epigraph.schema.parser.psi.SchemaImportStatement;
import com.sumologic.epigraph.schema.parser.psi.SchemaImports;
import com.sumologic.epigraph.schema.parser.psi.SchemaNamespaceDecl;
import com.sumologic.epigraph.schema.parser.psi.impl.SchemaElementFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ImportsManager {
  public static void addImport(@NotNull SchemaFile file, @NotNull String importToAdd) {
    // TODO this should return false if this would be a clashing import

    SchemaImports schemaImports = file.getImportsStatement();

    Project project = file.getProject();
    assert schemaImports != null;

    /*
    if (schemaImports == null) {
      // can we ever get here?
      schemaImports = SchemaElementFactory.createImports(project, importToAdd);

      SchemaNamespaceDecl namespaceDecl = file.getNamespaceDecl();
      if (namespaceDecl == null) {
        file.add(schemaImports);
      } else {
        file.addAfter(schemaImports, namespaceDecl);
      }

      file.addAfter(newline2(project), schemaImports);
    } else*/ {
      SchemaImportStatement importStatement = SchemaElementFactory.createImport(project, importToAdd);
      List<SchemaImportStatement> importStatementList = schemaImports.getImportStatementList();

      if (importStatementList.isEmpty()) {
        PsiElement e = schemaImports.add(importStatement);
        file.addAfter(newline2(project), schemaImports);
      } else {
        PsiElement e = schemaImports.addAfter(newline(project), importStatementList.get(importStatementList.size() - 1));
        e = schemaImports.addAfter(importStatement, e);
        file.addAfter(newline(project), e);
      }
    }

    // TODO(low) reformat?
  }

  private static PsiElement newline(Project project) {
    return SchemaElementFactory.createWhitespaces(project, "\n");
  }

  private static PsiElement newline2(Project project) {
    return SchemaElementFactory.createWhitespaces(project, "\n\n"); // TODO(low) rely on reformat instead of this
  }
}
