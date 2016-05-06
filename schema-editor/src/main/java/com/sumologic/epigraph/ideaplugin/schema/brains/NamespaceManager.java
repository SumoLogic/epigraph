package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFile;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqn;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaImportStatement;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaNamespaceDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class NamespaceManager {
  public static Fqn[] DEFAULT_NAMESPACES = new Fqn[]{
      new Fqn("epigraph", "types"),
      new Fqn("epigraph", "schema")
  };

  @NotNull
  public static Set<Fqn> getStarNamespaces(@NotNull PsiElement element) {
    SchemaFile schemaFile = PsiTreeUtil.getParentOfType(element, SchemaFile.class);
    if (schemaFile == null) return Collections.emptySet();

    Set<Fqn> res = new HashSet<>();

    List<SchemaImportStatement> importStatements = schemaFile.getImportStatements();
    for (SchemaImportStatement importStatement : importStatements) {
      SchemaFqn importFqn = importStatement.getFqn();
      if (importFqn != null && importStatement.getStarImportSuffix() != null)
        res.add(importFqn.getFqn());
    }

    return res;
  }

  @Nullable
  public static String getNamespace(@NotNull PsiElement element) {
    SchemaFile schemaFile = element instanceof SchemaFile ? (SchemaFile) element :
        PsiTreeUtil.getParentOfType(element, SchemaFile.class);

    if (schemaFile == null) return null;

    SchemaNamespaceDecl namespaceDecl = schemaFile.getNamespaceDecl();
    if (namespaceDecl == null) return null;

    SchemaFqn namespaceDeclFqn = namespaceDecl.getFqn();
    if (namespaceDeclFqn == null) return null;

    return namespaceDeclFqn.getFqn().toString();
  }

  /**
   * Finds all imports of the form `a.b.c.lastSegment` and returns them with last segment
   * removed, i.e. `a.b.c`
   */
  @NotNull
  public static Set<Fqn> getPrefixNamespacesWithLastSegmentRemoved(@NotNull PsiElement element, @Nullable String lastSegment) {
    SchemaFile schemaFile = PsiTreeUtil.getParentOfType(element, SchemaFile.class);
    if (schemaFile == null) return Collections.emptySet();

    Set<Fqn> res = new HashSet<>();

    List<SchemaImportStatement> importStatements = schemaFile.getImportStatements();
    for (SchemaImportStatement importStatement : importStatements) {
      SchemaFqn importFqn = importStatement.getFqn();
      if (importFqn != null && importStatement.getStarImportSuffix() == null) {
        Fqn fqn = importFqn.getFqn();
        if (lastSegment == null || lastSegment.equals(fqn.getLast())) {
          Fqn prefix = fqn.getPrefix();
          if (prefix != null)
            res.add(prefix);
        }
      }
    }

    return res;
  }

  /**
   * Finds all namespaces such that their fqn matches prefix and returns their segments following prefix.
   * For instance if prefix is 'a.b' and namespace is 'a.b.c.d' then 'c' will be collected.
   */
  @NotNull
  public static Set<String> getNamespaceSegmentsWithPrefix(@NotNull Project project, @NotNull String prefix) {
    String prefixWithDot = prefix.isEmpty() ? prefix : prefix + '.';
    List<SchemaNamespaceDecl> namespaces = SchemaIndexUtil.findNamespaces(project, prefixWithDot);
    if (namespaces.isEmpty()) return Collections.emptySet();

    int segmentsToRemove = prefix.isEmpty() ? 0 : prefix.length() - prefix.replace(".", "").length() + 1;

    Set<String> result = new HashSet<>();

    for (SchemaNamespaceDecl namespace : namespaces) {
      SchemaFqn schemaFqn = namespace.getFqn();
      assert schemaFqn != null;

      Fqn fqn = schemaFqn.getFqn();
      Fqn tail = fqn.removeHeadSegments(segmentsToRemove);

      if (!tail.isEmpty()) {
        result.add(tail.getFirst());
      }
    }

    return result;
  }
}
