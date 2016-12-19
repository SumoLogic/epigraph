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

package ws.epigraph.ideaplugin.schema.index;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.EdlNamespaceDecl;
import ws.epigraph.schema.parser.psi.EdlSupplementDef;
import ws.epigraph.schema.parser.psi.EdlTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class EdlIndexUtil {
  private EdlIndexUtil() {}

  @NotNull
  public static List<EdlTypeDef> findTypeDefs(@NotNull Project project,
                                                 @Nullable Collection<Qn> namespaces,
                                                 @Nullable Qn suffix,
                                                 @NotNull GlobalSearchScope scope) {
    return findTypeDefs(project, namespaces, suffix, scope, new AddAllProcessor<>());
  }

  @Nullable
  public static EdlTypeDef findTypeDef(@NotNull Project project,
                                          @NotNull Collection<Qn> namespaces,
                                          @NotNull Qn suffix,
                                          @NotNull GlobalSearchScope scope) {
    return findTypeDefs(project, namespaces, suffix, scope, new TakeFirstProcessor<>());
  }

  /**
   * Tries to find a single matching typedef. Returns {@code null} if there's none or
   * more than one matching.
   */
  @Nullable
  public static EdlTypeDef findSingleTypeDef(@NotNull Project project,
                                                @NotNull Collection<Qn> namespaces,
                                                @NotNull Qn suffix,
                                                @NotNull GlobalSearchScope scope) {
    return findTypeDefs(project, namespaces, suffix, scope, new TakeSingleProcessor<>());
  }

  private static <R> R findTypeDefs(@NotNull Project project,
                                    @Nullable Collection<Qn> namespaces,
                                    @Nullable Qn suffix,
                                    @NotNull GlobalSearchScope searchScope,
                                    @NotNull Processor<EdlTypeDef, R> processor) {

    if (namespaces == null) {
      if (suffix == null || suffix.size() == 1) {
        EdlShortTypeNameIndex index = EdlShortTypeNameIndex.EP_NAME.findExtension(EdlShortTypeNameIndex.class);
        assert index != null;

        Collection<String> shortNames;

        if (suffix == null) {
          shortNames = index.getAllKeys(project);
        } else {
          shortNames = Collections.singleton(suffix.toString());
        }

        for (String name : shortNames) {
          Collection<EdlTypeDef> typeDefs = index.get(name, project, searchScope);
          if (!processor.process(typeDefs)) break;
        }
      } else {
        EdlFullTypeNameIndex index = EdlFullTypeNameIndex.EP_NAME.findExtension(EdlFullTypeNameIndex.class);
        assert index != null;
        String suffixStr = "." + suffix.toString();

        Collection<String> fullNames = index.getAllKeys(project);
        for (String fullName : fullNames) {
          if (fullName.endsWith(suffixStr))
            if (!processor.process(index.get(fullName, project, searchScope))) break;
        }
      }

    } else {
      if (suffix == null) {
        EdlTypesByNamespaceIndex index =
            EdlTypesByNamespaceIndex.EP_NAME.findExtension(EdlTypesByNamespaceIndex.class);
        assert index != null;

        for (Qn namespace : namespaces) {
          Collection<EdlTypeDef> typeDefs = index.get(namespace.toString(), project, searchScope);
          if (!processor.process(typeDefs)) break;
        }
      } else {
        EdlFullTypeNameIndex index = EdlFullTypeNameIndex.EP_NAME.findExtension(EdlFullTypeNameIndex.class);
        assert index != null;

        for (Qn namespace : namespaces) {
          String fqn = namespace.append(suffix).toString();
          Collection<EdlTypeDef> typeDefs = index.get(fqn, project, searchScope);
          if (!processor.process(typeDefs)) break;
        }
      }
    }

    return processor.result();
  }

  @NotNull
  public static List<EdlTypeDef> findTypeDefs(@NotNull Project project, @NotNull Qn[] fqns, @NotNull GlobalSearchScope searchScope) {
    return findTypeDefs(project, fqns, new AddAllProcessor<>(), searchScope);
  }

  @Nullable
  public static EdlTypeDef findTypeDef(Project project, @NotNull Qn[] fqns, @NotNull GlobalSearchScope searchScope) {
    return findTypeDefs(project, fqns, new TakeFirstProcessor<>(), searchScope);
  }

  @Nullable
  public static EdlTypeDef findTypeDef(Project project, @NotNull Qn fqn, @NotNull GlobalSearchScope searchScope) {
    return findTypeDefs(project, new Qn[]{fqn}, new TakeFirstProcessor<>(), searchScope);
  }

  private static <R> R findTypeDefs(@NotNull Project project,
                                    @NotNull Qn[] fqns,
                                    @NotNull Processor<EdlTypeDef, R> processor,
                                    @NotNull GlobalSearchScope searchScope) {

    EdlFullTypeNameIndex index = EdlFullTypeNameIndex.EP_NAME.findExtension(EdlFullTypeNameIndex.class);
    assert index != null;

    for (Qn fqn : fqns) {
      Collection<EdlTypeDef> typeDefs = index.get(fqn.toString(), project, searchScope);
      if (!processor.process(typeDefs)) break;
    }

    return processor.result();
  }

  @NotNull
  public static List<EdlNamespaceDecl> findNamespaces(@NotNull Project project, @Nullable String namePrefix, @NotNull GlobalSearchScope searchScope) {
    // TODO cache all namespaces (if prefix is null)

    EdlNamespaceByNameIndex index = EdlNamespaceByNameIndex.EP_NAME.findExtension(EdlNamespaceByNameIndex.class);

    final List<EdlNamespaceDecl> result = new ArrayList<>();

    index.processAllKeys(project, namespaceFqn -> {
      if (namePrefix == null || namespaceFqn.startsWith(namePrefix)) {
        result.addAll(index.get(namespaceFqn, project, searchScope));
      }
      return true;
    });

    return result;
  }

  @Nullable
  public static EdlNamespaceDecl findNamespace(@NotNull Project project, @NotNull Qn namespace, @NotNull GlobalSearchScope searchScope) {
    EdlNamespaceByNameIndex index = EdlNamespaceByNameIndex.EP_NAME.findExtension(EdlNamespaceByNameIndex.class);

    Collection<EdlNamespaceDecl> namespaceDecls = index.get(namespace.toString(), project, searchScope);
    return namespaceDecls.isEmpty() ? null : namespaceDecls.iterator().next();
  }

  @NotNull
  public static List<EdlSupplementDef> findSupplementsBySource(@NotNull Project project, @NotNull EdlTypeDef source) {
    GlobalSearchScope allScope = GlobalSearchScope.allScope(project);

    String name = source.getName();
    if (name == null) return Collections.emptyList();

    EdlSupplementBySourceIndex index = EdlSupplementBySourceIndex.EP_NAME.findExtension(EdlSupplementBySourceIndex.class);

    final List<EdlSupplementDef> result = new ArrayList<>();

    index.processAllKeys(project, sourceShortName -> {
      Collection<EdlSupplementDef> edlSupplementDefs = index.get(sourceShortName, project, allScope);
      for (EdlSupplementDef edlSupplementDef : edlSupplementDefs) {
        ProgressManager.checkCanceled();
        EdlTypeDef s = edlSupplementDef.source();
        GlobalSearchScope supplementScope = EdlSearchScopeUtil.getSearchScope(edlSupplementDef);
        if (source == s && EdlSearchScopeUtil.isInScope(supplementScope, source)) result.add(edlSupplementDef);
      }
      return true;
    });

    return result;
  }

  @NotNull
  public static List<EdlSupplementDef> findSupplementsBySupplemented(@NotNull Project project, @NotNull EdlTypeDef supplemented) {
    GlobalSearchScope allScope = GlobalSearchScope.allScope(project);

    String name = supplemented.getName();
    if (name == null) return Collections.emptyList();

    EdlSupplementBySupplementedIndex index = EdlSupplementBySupplementedIndex.EP_NAME.findExtension(EdlSupplementBySupplementedIndex.class);

    final List<EdlSupplementDef> result = new ArrayList<>();

    index.processAllKeys(project, sourceShortName -> {
      Collection<EdlSupplementDef> edlSupplementDefs = index.get(sourceShortName, project, allScope);
      // check all supplement defs
      for (EdlSupplementDef edlSupplementDef : edlSupplementDefs) {
        ProgressManager.checkCanceled();
        // supplemented must be visible by supplement
        GlobalSearchScope supplementScope = EdlSearchScopeUtil.getSearchScope(edlSupplementDef);
        if (EdlSearchScopeUtil.isInScope(supplementScope, supplemented)) {
          // check their supplemented lists
          List<EdlTypeDef> ss = edlSupplementDef.supplemented();
          for (EdlTypeDef s : ss) {
            // try to find `supplemented` among them
            if (supplemented == s) {
              result.add(edlSupplementDef);
              break;
            }
          }
        }
      }
      return true;
    });

    return result;
  }

  private interface Processor<T, R> {
    boolean process(Collection<T> items);

    R result();
  }

  private static class AddAllProcessor<T> implements Processor<T, List<T>> {
    private final ArrayList<T> result = new ArrayList<>();

    @Override
    public boolean process(Collection<T> items) {
      result.addAll(items);
      return true;
    }

    @Override
    public List<T> result() {
      return result;
    }
  }

  private static class TakeFirstProcessor<T> implements Processor<T, T> {
    private T result = null;


    @Override
    public boolean process(Collection<T> items) {
      if (items.isEmpty()) return true;
      result = items.iterator().next();
      return false;
    }

    @Override
    public T result() {
      return result;
    }
  }

  private static class TakeSingleProcessor<T> implements Processor<T, T> {
    private T result = null;


    @Override
    public boolean process(Collection<T> items) {
      if (items.isEmpty()) return true;

      if (result != null || items.size() > 1) {
        result = null;
        return false;
      }

      result = items.iterator().next();
      return true;
    }

    @Override
    public T result() {
      return result;
    }
  }
}
