package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.psi.SchemaFile;
import com.sumologic.epigraph.schema.parser.psi.SchemaFqn;
import com.sumologic.epigraph.schema.parser.psi.SchemaImportStatement;
import com.sumologic.epigraph.schema.parser.psi.SchemaNamespaceDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class NamespaceManager {
  public static Fqn[] DEFAULT_NAMESPACES = new Fqn[]{
      new Fqn("epigraph")
//      new Fqn("epigraph", "types"),
//      new Fqn("epigraph", "schema")
  };

  public static boolean isDefaultNamespace(@NotNull String namespace) {
    // NB: keep in sync with the above
    return "epigraph".equals(namespace);

//    for (Fqn defaultNamespace : DEFAULT_NAMESPACES) {
//      if (defaultNamespace.toString().equals(namespace)) return true;
//    }
//    return false;
  }

  @NotNull
  public static Set<Fqn> getStarNamespaces(@NotNull PsiElement element) {
    SchemaFile schemaFile = getSchemaFile(element);
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
    SchemaFile schemaFile = getSchemaFile(element);

    if (schemaFile == null) return null;

    SchemaNamespaceDecl namespaceDecl = schemaFile.getNamespaceDecl();
    if (namespaceDecl == null) return null;

    SchemaFqn namespaceDeclFqn = namespaceDecl.getFqn();
    if (namespaceDeclFqn == null) return null;

    return namespaceDeclFqn.getFqn().toString();
  }

  public static Set<Fqn> getVisibleNamespaces(@NotNull PsiElement element, boolean includeStarImports) {
    SchemaFile schemaFile = getSchemaFile(element);
    if (schemaFile == null) return Collections.emptySet();

    Set<Fqn> res = new HashSet<>();

    List<SchemaImportStatement> importStatements = schemaFile.getImportStatements();
    for (SchemaImportStatement importStatement : importStatements) {
      SchemaFqn importFqn = importStatement.getFqn();
      if (importFqn != null) {
        boolean isStarImport = importStatement.getStarImportSuffix() != null;
        if (!isStarImport || includeStarImports) res.add(importFqn.getFqn());
      }
    }

    SchemaNamespaceDecl namespaceDecl = schemaFile.getNamespaceDecl();
    if (namespaceDecl != null) {
      SchemaFqn namespaceDeclFqn = namespaceDecl.getFqn();
      if (namespaceDeclFqn != null) res.add(namespaceDeclFqn.getFqn());
    }

    return res;
  }

  @NotNull
  public static Set<Fqn> getNamespacesByPrefix(@NotNull Project project, @Nullable String prefix) {
    String prefixWithDot = prefix == null ? null : prefix.isEmpty() ? prefix : prefix + '.';
    List<SchemaNamespaceDecl> namespaces = SchemaIndexUtil.findNamespaces(project, prefixWithDot);
    if (namespaces.isEmpty()) return Collections.emptySet();

    return namespaces.stream().map(SchemaNamespaceDecl::getFqn2).collect(Collectors.toSet());
  }

  @Nullable
  private static SchemaFile getSchemaFile(@NotNull PsiElement element) {
    return element instanceof SchemaFile ? (SchemaFile) element :
        PsiTreeUtil.getParentOfType(element, SchemaFile.class);
  }
}
