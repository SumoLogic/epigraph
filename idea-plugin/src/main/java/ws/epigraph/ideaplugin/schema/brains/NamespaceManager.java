/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import ws.epigraph.ideaplugin.schema.index.EdlIndexUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.EdlFile;
import ws.epigraph.schema.parser.psi.EdlQn;
import ws.epigraph.schema.parser.psi.EdlImportStatement;
import ws.epigraph.schema.parser.psi.EdlNamespaceDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class NamespaceManager {
  public static Qn[] DEFAULT_NAMESPACES = new Qn[]{new Qn("epigraph")};
//  public static List<Qn> DEFAULT_NAMESPACES_LIST = Collections.unmodifiableList(Arrays.asList(DEFAULT_NAMESPACES));

  private final Project project;
  private Collection<EdlNamespaceDecl> allNamespaces;

  public NamespaceManager(@NotNull Project project) {
    this.project = project;
    PsiManager.getInstance(project).addPsiTreeChangeListener(new InvalidationListener(), project);
  }

  public static NamespaceManager getNamespaceManager(@NotNull Project project) {
    return project.getComponent(NamespaceManager.class);
  }

  @NotNull
  public Collection<EdlNamespaceDecl> getAllNamespaces(@NotNull GlobalSearchScope searchScope) {
    Collection<EdlNamespaceDecl> res = allNamespaces;
    if (res != null) return res;

    res = getNamespacesByPrefix(project, null, false, searchScope);
    allNamespaces = res;
    return res;
  }

  @Nullable
  public static Qn getNamespace(@NotNull PsiElement element) {
    EdlFile edlFile = getEdlFile(element);

    if (edlFile == null) return null;

    EdlNamespaceDecl namespaceDecl = edlFile.getNamespaceDecl();
    if (namespaceDecl == null) return null;

    EdlQn namespaceDeclQn = namespaceDecl.getQn();
    if (namespaceDeclQn == null) return null;

    return namespaceDeclQn.getQn();
  }

  @NotNull
  public static List<Qn> getImportedNamespaces(@NotNull PsiElement element) {
    EdlFile edlFile = getEdlFile(element);
    if (edlFile == null) return Collections.emptyList();

    List<Qn> res = new ArrayList<>();

    List<EdlImportStatement> importStatements = edlFile.getImportStatements();
    for (EdlImportStatement importStatement : importStatements) {
      EdlQn importQn = importStatement.getQn();
      if (importQn != null) res.add(importQn.getQn());
    }
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
  public static List<EdlNamespaceDecl> getNamespacesByPrefix(@NotNull Project project,
                                                                @Nullable Qn prefix,
                                                                boolean returnSingleExactMatch,
                                                                @NotNull GlobalSearchScope searchScope) {
    String prefixStr = prefix == null ? null : prefix.toString();
    if (returnSingleExactMatch && prefix != null) {
      assert prefixStr != null;
      List<EdlNamespaceDecl> namespaces = EdlIndexUtil.findNamespaces(project, prefixStr, searchScope);
      // try to find a namespace which is exactly our prefix
      for (EdlNamespaceDecl namespace : namespaces) {
        //noinspection ConstantConditions
        if (prefix.equals(namespace.getFqn()))
          return Collections.singletonList(namespace);
      }

      // we have to filter namespaces again. When looking for 'foo.bar.baz' we don't want to get
      // 'foo.bar.bazzzz', but we're interested in 'foo.bar.baz.qux'

      //noinspection ConstantConditions
      return namespaces.stream().filter(ns -> ns.getFqn().startsWith(prefix)).collect(Collectors.toList());
    } else {
      String prefixWithDot = prefix == null ? null : prefix.isEmpty() ? prefix.toString() : prefix.toString() + '.';
      return EdlIndexUtil.findNamespaces(project, prefixWithDot, searchScope);
    }
  }

  @Nullable
  private static EdlFile getEdlFile(@NotNull PsiElement element) {
    return element instanceof EdlFile ? (EdlFile) element :
        PsiTreeUtil.getParentOfType(element, EdlFile.class);
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

//      final PsiElement element = event.getElement();
      final PsiElement child = event.getChild();
//      final PsiElement parent = child == null ? null : child.getParent();

      if (child instanceof PsiWhiteSpace) return;

      // namespace changed
      if (PsiTreeUtil.getParentOfType(child, EdlNamespaceDecl.class) != null) {
        invalidate = true;
      }

      // file added/removed
      if (child instanceof EdlFile) {
        invalidate = true;
      }

      // something else?

      if (invalidate)
        allNamespaces = null;
    }
  }
}
