package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
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
  public static String getCurrentNamespace(@NotNull PsiElement element) {
    SchemaFile schemaFile = PsiTreeUtil.getParentOfType(element, SchemaFile.class);
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
  public static Set<Fqn> getPrefixNamespacesWithLastSegmentRemoved(@NotNull PsiElement element, @NotNull String lastSegment) {
    SchemaFile schemaFile = PsiTreeUtil.getParentOfType(element, SchemaFile.class);
    if (schemaFile == null) return Collections.emptySet();

    Set<Fqn> res = new HashSet<>();

    List<SchemaImportStatement> importStatements = schemaFile.getImportStatements();
    for (SchemaImportStatement importStatement : importStatements) {
      SchemaFqn importFqn = importStatement.getFqn();
      if (importFqn != null && importStatement.getStarImportSuffix() == null) {
        Fqn fqn = importFqn.getFqn();
        if (lastSegment.equals(fqn.getLast())) {
          Fqn prefix = fqn.getPrefix();
          if (prefix != null)
            res.add(prefix);
        }
      }
    }

    return res;
  }
}
