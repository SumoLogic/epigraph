package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
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
      new Fqn("epigraph", "String"),
      new Fqn("epigraph", "Integer"),
      new Fqn("epigraph", "Long"),
      new Fqn("epigraph", "Double"),
      new Fqn("epigraph", "Boolean"),
  };

  public static List<Fqn> DEFAULT_NAMESPACES_LIST = Collections.unmodifiableList(Arrays.asList(DEFAULT_NAMESPACES));

  private final Project project;
  private Collection<SchemaNamespaceDecl> allNamespaces;

  public NamespaceManager(@NotNull Project project) {
    this.project = project;
    PsiManager.getInstance(project).addPsiTreeChangeListener(new InvalidationListener(), project);
  }

  public static NamespaceManager getNamespaceManager(@NotNull Project project) {
    return project.getComponent(NamespaceManager.class);
  }

  @NotNull
  public Collection<SchemaNamespaceDecl> getAllNamespaces() {
    Collection<SchemaNamespaceDecl> res = allNamespaces;
    if (res != null) return res;

    res = getNamespacesByPrefix(project, null, false);
    allNamespaces = res;
    return res;
  }

  @Nullable
  public static Fqn getNamespace(@NotNull PsiElement element) {
    SchemaFile schemaFile = getSchemaFile(element);

    if (schemaFile == null) return null;

    SchemaNamespaceDecl namespaceDecl = schemaFile.getNamespaceDecl();
    if (namespaceDecl == null) return null;

    SchemaFqn namespaceDeclFqn = namespaceDecl.getFqn();
    if (namespaceDeclFqn == null) return null;

    return namespaceDeclFqn.getFqn();
  }

  public static List<Fqn> getImportedNamespaces(@NotNull PsiElement element) {
    SchemaFile schemaFile = getSchemaFile(element);
    if (schemaFile == null) return Collections.emptyList();


    List<Fqn> res = new ArrayList<>();

    // 1. imported namespaces
    List<SchemaImportStatement> importStatements = schemaFile.getImportStatements();
    for (SchemaImportStatement importStatement : importStatements) {
      SchemaFqn importFqn = importStatement.getFqn();
      if (importFqn != null) res.add(importFqn.getFqn());
    }

//    // 2. default namespaces
//    res.addAll(DEFAULT_NAMESPACES_LIST);
//
//    // 3. current namespace
//    SchemaNamespaceDecl namespaceDecl = schemaFile.getNamespaceDecl();
//    if (namespaceDecl != null) {
//      SchemaFqn namespaceDeclFqn = namespaceDecl.getFqn();
//      if (namespaceDeclFqn != null) res.add(namespaceDeclFqn.getFqn());
//    }

    return res;
  }

  /**
   * Finds namespaces matching {@code prefix}.
   *
   * @param project                current project
   * @param prefix                 prefix to look for. Return all namespaces if {@code null}. Consider using
   *                               (caching) {@code #getAllNamespaces} in this case.
   * @param returnSingleExactMatch if set to {@code true} and there exists a namespace which is exactly our prefix
   *                               (without implicit dot), then a singleton list with only this namespace is returned.
   * @return collection of matching namespaces.
   */
  @NotNull
  public static List<SchemaNamespaceDecl> getNamespacesByPrefix(@NotNull Project project,
                                                                @Nullable Fqn prefix,
                                                                boolean returnSingleExactMatch) {
    String prefixStr = prefix == null ? null : prefix.toString();
    if (returnSingleExactMatch && prefix != null) {
      assert prefixStr != null;
      List<SchemaNamespaceDecl> namespaces = SchemaIndexUtil.findNamespaces(project, prefixStr);
      // try to find a namespace which is exactly our prefix
      for (SchemaNamespaceDecl namespace : namespaces) {
        //noinspection ConstantConditions
        if (prefix.equals(namespace.getFqn2()))
          return Collections.singletonList(namespace);
      }

      // we have to filter namespaces again. When looking for 'foo.bar.baz' we don't want to get
      // 'foo.bar.bazzzz', but we're interested in 'foo.bar.baz.qux'

      //noinspection ConstantConditions
      return namespaces.stream().filter(ns -> ns.getFqn2().startsWith(prefix)).collect(Collectors.toList());
    } else {
      String prefixWithDot = prefix == null ? null : prefix.isEmpty() ? prefix.toString() : prefix.toString() + '.';
      return SchemaIndexUtil.findNamespaces(project, prefixWithDot);
    }
  }

  @Nullable
  private static SchemaFile getSchemaFile(@NotNull PsiElement element) {
    return element instanceof SchemaFile ? (SchemaFile) element :
        PsiTreeUtil.getParentOfType(element, SchemaFile.class);
  }

  // --------------------------------

  private class InvalidationListener extends PsiTreeChangeAdapter {
    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
      handle(event);
    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {
      handle(event);
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
      handle(event);
    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
      handle(event);
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
      handle(event);
    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
      handle(event);
    }

    private void handle(@NotNull PsiTreeChangeEvent event) {
      boolean invalidate = false;

      final PsiElement element = event.getElement();
      final PsiElement child = event.getChild();
      final PsiElement parent = child == null ? null : child.getParent();

      if (child instanceof PsiWhiteSpace) return;

      // namespace changed
      if (PsiTreeUtil.getParentOfType(child, SchemaNamespaceDecl.class) != null) {
        invalidate = true;
      }

      // something else?

      if (invalidate)
        allNamespaces = null;
    }
  }
}
