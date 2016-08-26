package com.sumologic.epigraph.ideaplugin.schema.index;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.parser.psi.EpigraphSupplementDef;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import io.epigraph.lang.parser.psi.EpigraphNamespaceDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaIndexUtil {
  @NotNull
  public static List<EpigraphTypeDef> findTypeDefs(@NotNull Project project,
                                                   @Nullable Collection<Fqn> namespaces,
                                                   @Nullable Fqn suffix,
                                                   @NotNull GlobalSearchScope scope) {
    return findTypeDefs(project, namespaces, suffix, scope, new AddAllProcessor<>());
  }

  @Nullable
  public static EpigraphTypeDef findTypeDef(@NotNull Project project,
                                            @NotNull Collection<Fqn> namespaces,
                                            @NotNull Fqn suffix,
                                            @NotNull GlobalSearchScope scope) {
    return findTypeDefs(project, namespaces, suffix, scope, new TakeFirstProcessor<>());
  }

  /**
   * Tries to find a single matching typedef. Returns {@code null} if there's none or
   * more than one matching.
   */
  @Nullable
  public static EpigraphTypeDef findSingleTypeDef(@NotNull Project project,
                                                  @NotNull Collection<Fqn> namespaces,
                                                  @NotNull Fqn suffix,
                                                  @NotNull GlobalSearchScope scope) {
    return findTypeDefs(project, namespaces, suffix, scope, new TakeSingleProcessor<>());
  }

  private static <R> R findTypeDefs(@NotNull Project project,
                                    @Nullable Collection<Fqn> namespaces,
                                    @Nullable Fqn suffix,
                                    @NotNull GlobalSearchScope searchScope,
                                    @NotNull Processor<EpigraphTypeDef, R> processor) {

    if (namespaces != null) {
      if (suffix != null) {
        SchemaFullTypeNameIndex index = SchemaFullTypeNameIndex.EP_NAME.findExtension(SchemaFullTypeNameIndex.class);
        assert index != null;

        for (Fqn namespace : namespaces) {
          String fqn = namespace.append(suffix).toString();
          Collection<EpigraphTypeDef> typeDefs = index.get(fqn, project, searchScope);
          if (!processor.process(typeDefs)) break;
        }
      } else {
        SchemaTypesByNamespaceIndex index = SchemaTypesByNamespaceIndex.EP_NAME.findExtension(SchemaTypesByNamespaceIndex.class);
        assert index != null;

        for (Fqn namespace : namespaces) {
          Collection<EpigraphTypeDef> typeDefs = index.get(namespace.toString(), project, searchScope);
          if (!processor.process(typeDefs)) break;
        }
      }
    } else {
      if (suffix == null || suffix.size() == 1) {
        SchemaShortTypeNameIndex index = SchemaShortTypeNameIndex.EP_NAME.findExtension(SchemaShortTypeNameIndex.class);
        assert index != null;

        Collection<String> shortNames;

        if (suffix != null) {
          shortNames = Collections.singleton(suffix.toString());
        } else {
          shortNames = index.getAllKeys(project);
        }

        for (String name : shortNames) {
          Collection<EpigraphTypeDef> typeDefs = index.get(name, project, searchScope);
          if (!processor.process(typeDefs)) break;
        }
      } else {
        SchemaFullTypeNameIndex index = SchemaFullTypeNameIndex.EP_NAME.findExtension(SchemaFullTypeNameIndex.class);
        assert index != null;
        String suffixStr = "." + suffix.toString();

        Collection<String> fullNames = index.getAllKeys(project);
        for (String fullName : fullNames) {
          if (fullName.endsWith(suffixStr))
            if (!processor.process(index.get(fullName, project, searchScope))) break;
        }
      }

    }

    return processor.result();
  }

  @NotNull
  public static List<EpigraphTypeDef> findTypeDefs(@NotNull Project project, @NotNull Fqn[] fqns, @NotNull GlobalSearchScope searchScope) {
    return findTypeDefs(project, fqns, new AddAllProcessor<>(), searchScope);
  }

  @Nullable
  public static EpigraphTypeDef findTypeDef(Project project, @NotNull Fqn[] fqns, @NotNull GlobalSearchScope searchScope) {
    return findTypeDefs(project, fqns, new TakeFirstProcessor<>(), searchScope);
  }

  @Nullable
  public static EpigraphTypeDef findTypeDef(Project project, @NotNull Fqn fqn, @NotNull GlobalSearchScope searchScope) {
    return findTypeDefs(project, new Fqn[]{fqn}, new TakeFirstProcessor<>(), searchScope);
  }

  private static <R> R findTypeDefs(@NotNull Project project,
                                    @NotNull Fqn[] fqns,
                                    @NotNull Processor<EpigraphTypeDef, R> processor,
                                    @NotNull GlobalSearchScope searchScope) {

    SchemaFullTypeNameIndex index = SchemaFullTypeNameIndex.EP_NAME.findExtension(SchemaFullTypeNameIndex.class);
    assert index != null;

    for (Fqn fqn : fqns) {
      Collection<EpigraphTypeDef> typeDefs = index.get(fqn.toString(), project, searchScope);
      if (!processor.process(typeDefs)) break;
    }

    return processor.result();
  }

  @NotNull
  public static List<EpigraphNamespaceDecl> findNamespaces(@NotNull Project project, @Nullable String namePrefix, @NotNull GlobalSearchScope searchScope) {
    // TODO cache all namespaces (if prefix is null)

    SchemaNamespaceByNameIndex index = SchemaNamespaceByNameIndex.EP_NAME.findExtension(SchemaNamespaceByNameIndex.class);

    final List<EpigraphNamespaceDecl> result = new ArrayList<>();

    index.processAllKeys(project, namespaceFqn -> {
      if (namePrefix == null || namespaceFqn.startsWith(namePrefix)) {
        result.addAll(index.get(namespaceFqn, project, searchScope));
      }
      return true;
    });

    return result;
  }

  @Nullable
  public static EpigraphNamespaceDecl findNamespace(@NotNull Project project, @NotNull Fqn namespace, @NotNull GlobalSearchScope searchScope) {
    SchemaNamespaceByNameIndex index = SchemaNamespaceByNameIndex.EP_NAME.findExtension(SchemaNamespaceByNameIndex.class);

    Collection<EpigraphNamespaceDecl> namespaceDecls = index.get(namespace.toString(), project, searchScope);
    return namespaceDecls.isEmpty() ? null : namespaceDecls.iterator().next();
  }

  @NotNull
  public static List<EpigraphSupplementDef> findSupplementsBySource(@NotNull Project project, @NotNull EpigraphTypeDef source) {
    GlobalSearchScope allScope = GlobalSearchScope.allScope(project);

    String name = source.getName();
    if (name == null) return Collections.emptyList();

    SchemaSupplementBySourceIndex index = SchemaSupplementBySourceIndex.EP_NAME.findExtension(SchemaSupplementBySourceIndex.class);

    final List<EpigraphSupplementDef> result = new ArrayList<>();

    index.processAllKeys(project, sourceShortName -> {
      Collection<EpigraphSupplementDef> epigraphSupplementDefs = index.get(sourceShortName, project, allScope);
      for (EpigraphSupplementDef epigraphSupplementDef : epigraphSupplementDefs) {
        ProgressManager.checkCanceled();
        EpigraphTypeDef s = epigraphSupplementDef.source();
        GlobalSearchScope supplementScope = SchemaSearchScopeUtil.getSearchScope(epigraphSupplementDef);
        if (source == s && SchemaSearchScopeUtil.isInScope(supplementScope, source)) result.add(epigraphSupplementDef);
      }
      return true;
    });

    return result;
  }

  @NotNull
  public static List<EpigraphSupplementDef> findSupplementsBySupplemented(@NotNull Project project, @NotNull EpigraphTypeDef supplemented) {
    GlobalSearchScope allScope = GlobalSearchScope.allScope(project);

    String name = supplemented.getName();
    if (name == null) return Collections.emptyList();

    SchemaSupplementBySupplementedIndex index = SchemaSupplementBySupplementedIndex.EP_NAME.findExtension(SchemaSupplementBySupplementedIndex.class);

    final List<EpigraphSupplementDef> result = new ArrayList<>();

    index.processAllKeys(project, sourceShortName -> {
      Collection<EpigraphSupplementDef> epigraphSupplementDefs = index.get(sourceShortName, project, allScope);
      // check all supplement defs
      for (EpigraphSupplementDef epigraphSupplementDef : epigraphSupplementDefs) {
        ProgressManager.checkCanceled();
        // supplemented must be visible by supplement
        GlobalSearchScope supplementScope = SchemaSearchScopeUtil.getSearchScope(epigraphSupplementDef);
        if (SchemaSearchScopeUtil.isInScope(supplementScope, supplemented)) {
          // check their supplemented lists
          List<EpigraphTypeDef> ss = epigraphSupplementDef.supplemented();
          for (EpigraphTypeDef s : ss) {
            // try to find `supplemented` among them
            if (supplemented == s) {
              result.add(epigraphSupplementDef);
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
