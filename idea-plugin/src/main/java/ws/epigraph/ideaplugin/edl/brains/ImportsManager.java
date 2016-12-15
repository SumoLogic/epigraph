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

package ws.epigraph.ideaplugin.edl.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.MultiMap;
import ws.epigraph.ideaplugin.edl.index.EdlIndexUtil;
import ws.epigraph.ideaplugin.edl.index.EdlSearchScopeUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.edl.parser.psi.*;
import ws.epigraph.edl.parser.psi.impl.EdlElementFactory;
import ws.epigraph.edl.parser.psi.impl.EdlPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ws.epigraph.lang.DefaultImports.DEFAULT_IMPORTS;
import static ws.epigraph.lang.DefaultImports.DEFAULT_IMPORTS_LIST;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ImportsManager {
  private ImportsManager() {}

  public static void addImport(@NotNull EdlFile file, @NotNull String importToAdd) {
    // TODO this should return false if this would be a clashing import

    EdlImports edlImports = file.getImportsStatement();

    Project project = file.getProject();
    assert edlImports != null;

    /*
    if (edlTypeImports == null) {
      // can we ever get here?
      edlTypeImports = EdlElementFactory.createImports(project, importToAdd);

      EdlNamespaceDecl namespaceDecl = file.getNamespaceDecl();
      if (namespaceDecl == null) {
        file.add(edlTypeImports);
      } else {
        file.addAfter(edlTypeImports, namespaceDecl);
      }

      file.addAfter(newline2(project), edlTypeImports);
    } else*/
    {
      EdlImportStatement importStatement = EdlElementFactory.createImport(project, importToAdd);
      List<EdlImportStatement> importStatementList = edlImports.getImportStatementList();

      if (importStatementList.isEmpty()) {
        edlImports.add(importStatement);
        file.addAfter(newline2(project), edlImports);
      } else {
        PsiElement e =
            edlImports.addAfter(newline(project), importStatementList.get(importStatementList.size() - 1));
        e = edlImports.addAfter(importStatement, e);
        file.addAfter(newline(project), e);
      }
    }

    // TODO(low) reformat?
  }

  private static PsiElement newline(Project project) {
    return EdlElementFactory.createWhitespaces(project, "\n");
  }

  private static PsiElement newline2(Project project) {
    return EdlElementFactory.createWhitespaces(project, "\n\n"); // TODO(low) rely on reformat instead of this
  }

  public static List<Qn> findImportsBySuffix(@NotNull EdlFile file, @NotNull Qn suffix) {
    EdlImports edlImports = file.getImportsStatement();
    if (edlImports == null) return Collections.emptyList();

    List<EdlImportStatement> importStatements = edlImports.getImportStatementList();
    if (importStatements.isEmpty()) return Collections.emptyList();

    //noinspection ConstantConditions
    Stream<Qn> explicitImports = importStatements
        .stream()
        .filter(st -> {
          EdlQn sqn = st.getQn();
          Qn qn = sqn == null ? null : sqn.getQn();
          return qn != null && qn.endsWith(suffix);
        })
        .map(st -> st.getQn().getQn());

    Stream<? extends Qn> implicitImports =
        DEFAULT_IMPORTS_LIST.stream().filter(qn -> qn.endsWith(suffix));

    return Stream.concat(explicitImports, implicitImports).collect(Collectors.toList());
  }

  public static Set<EdlImportStatement> findUnusedImports(@NotNull EdlFile file) {
    EdlImports edlImports = file.getImportsStatement();
    if (edlImports == null) return Collections.emptySet();

    List<EdlImportStatement> importStatements = edlImports.getImportStatementList();
    if (importStatements.isEmpty()) return Collections.emptySet();

    MultiMap<Qn, EdlImportStatement> importsByQn = getImportsByQn(importStatements);
    for (Qn defaultImport : DEFAULT_IMPORTS) importsByQn.remove(defaultImport);

    // first add all imports, then remove those actually used
    final Set<EdlImportStatement> res = new HashSet<>(importsByQn.values());
    final GlobalSearchScope searchScope = EdlSearchScopeUtil.getSearchScope(file);

    EdlVisitor visitor = new EdlVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        super.visitElement(element);
        element.acceptChildren(this);
      }

      @Override
      public void visitQnTypeRef(@NotNull EdlQnTypeRef typeRef) {
        super.visitQnTypeRef(typeRef);
        PsiReference reference = EdlPsiImplUtil.getReference(typeRef);
        if (reference instanceof EdlQnReference) {
          EdlQnReference edlQnReference = (EdlQnReference) reference;
          EdlQnReferenceResolver resolver = edlQnReference.getResolver();
          Qn targetQn = resolver.getTargetTypeDefQn(typeRef.getProject());

          if (targetQn != null) {
            Qn input = resolver.getInput();
            if (!input.equals(targetQn)) {
              String inputFirstSegment = input.first();
              assert inputFirstSegment != null;

              importsByQn.entrySet().stream()
                  .filter(entry -> inputFirstSegment.equals(entry.getKey().last()))
                  .forEach(entry -> res.removeAll(entry.getValue()));
            }
          }
        }
      }
    };

    file.accept(visitor);

    // add all unresolved imports (unresolved => unused)
    final Project project = file.getProject();
    for (Map.Entry<Qn, Collection<EdlImportStatement>> entry : importsByQn.entrySet()) {
      EdlTypeDef typeDef = EdlIndexUtil.findTypeDef(project, entry.getKey(), searchScope);
      if (typeDef == null && EdlIndexUtil.findNamespace(project, entry.getKey(), searchScope) == null) {
        res.addAll(entry.getValue());
      }
    }

    return res;
  }

  public static Runnable buildImportOptimizer(@NotNull final EdlFile file) {
    final List<Qn> optimizedImports = getOptimizedImports(file);

    return () -> {
      List<EdlImportStatement> importStatements = file.getImportStatements();
      importStatements.forEach(PsiElement::delete);

      for (Qn qn : optimizedImports)
        addImport(file, qn.toString());
    };
  }

  static List<Qn> getOptimizedImports(@NotNull EdlFile file) {
    // de-duplicated imports without implicits
    Set<Qn> qns = file.getImportStatements().stream()
        .map(EdlImportStatement::getQn)
        .filter(Objects::nonNull)
        .map(EdlQn::getQn)
        .filter(qn -> !DEFAULT_IMPORTS_LIST.contains(qn))
        .collect(Collectors.toSet());

    //noinspection ConstantConditions
    findUnusedImports(file).forEach(is -> qns.remove(is.getQn().getQn()));

    final List<Qn> res = new ArrayList<>(qns.size());
    res.addAll(qns);
    Collections.sort(res);

    return res;
  }

  @NotNull
  public static MultiMap<Qn, EdlImportStatement> getImportsByQn(List<EdlImportStatement> importStatements) {
    MultiMap<Qn, EdlImportStatement> importsByQn = new MultiMap<>();
    for (EdlImportStatement importStatement : importStatements) {
      EdlQn edlQn = importStatement.getQn();
      if (edlQn != null) {
        Qn qn = edlQn.getQn();
        importsByQn.putValue(qn, importStatement);
      }
    }
    return importsByQn;
  }

}
