package ws.epigraph.ideaplugin.schema.index;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.SchemaNamespaceDecl;
import ws.epigraph.schema.parser.psi.SchemaSupplementDef;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaIndexUtil {
  @NotNull
  public static List<SchemaTypeDef> findTypeDefs(@NotNull Project project,
                                                 @Nullable Collection<Qn> namespaces,
                                                 @Nullable Qn suffix,
                                                 @NotNull GlobalSearchScope scope) {
    return findTypeDefs(project, namespaces, suffix, scope, new AddAllProcessor<>());
  }

  @Nullable
  public static SchemaTypeDef findTypeDef(@NotNull Project project,
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
  public static SchemaTypeDef findSingleTypeDef(@NotNull Project project,
                                                @NotNull Collection<Qn> namespaces,
                                                @NotNull Qn suffix,
                                                @NotNull GlobalSearchScope scope) {
    return findTypeDefs(project, namespaces, suffix, scope, new TakeSingleProcessor<>());
  }

  private static <R> R findTypeDefs(@NotNull Project project,
                                    @Nullable Collection<Qn> namespaces,
                                    @Nullable Qn suffix,
                                    @NotNull GlobalSearchScope searchScope,
                                    @NotNull Processor<SchemaTypeDef, R> processor) {

    if (namespaces != null) {
      if (suffix != null) {
        SchemaFullTypeNameIndex index = SchemaFullTypeNameIndex.EP_NAME.findExtension(SchemaFullTypeNameIndex.class);
        assert index != null;

        for (Qn namespace : namespaces) {
          String fqn = namespace.append(suffix).toString();
          Collection<SchemaTypeDef> typeDefs = index.get(fqn, project, searchScope);
          if (!processor.process(typeDefs)) break;
        }
      } else {
        SchemaTypesByNamespaceIndex index = SchemaTypesByNamespaceIndex.EP_NAME.findExtension(SchemaTypesByNamespaceIndex.class);
        assert index != null;

        for (Qn namespace : namespaces) {
          Collection<SchemaTypeDef> typeDefs = index.get(namespace.toString(), project, searchScope);
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
          Collection<SchemaTypeDef> typeDefs = index.get(name, project, searchScope);
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
  public static List<SchemaTypeDef> findTypeDefs(@NotNull Project project, @NotNull Qn[] fqns, @NotNull GlobalSearchScope searchScope) {
    return findTypeDefs(project, fqns, new AddAllProcessor<>(), searchScope);
  }

  @Nullable
  public static SchemaTypeDef findTypeDef(Project project, @NotNull Qn[] fqns, @NotNull GlobalSearchScope searchScope) {
    return findTypeDefs(project, fqns, new TakeFirstProcessor<>(), searchScope);
  }

  @Nullable
  public static SchemaTypeDef findTypeDef(Project project, @NotNull Qn fqn, @NotNull GlobalSearchScope searchScope) {
    return findTypeDefs(project, new Qn[]{fqn}, new TakeFirstProcessor<>(), searchScope);
  }

  private static <R> R findTypeDefs(@NotNull Project project,
                                    @NotNull Qn[] fqns,
                                    @NotNull Processor<SchemaTypeDef, R> processor,
                                    @NotNull GlobalSearchScope searchScope) {

    SchemaFullTypeNameIndex index = SchemaFullTypeNameIndex.EP_NAME.findExtension(SchemaFullTypeNameIndex.class);
    assert index != null;

    for (Qn fqn : fqns) {
      Collection<SchemaTypeDef> typeDefs = index.get(fqn.toString(), project, searchScope);
      if (!processor.process(typeDefs)) break;
    }

    return processor.result();
  }

  @NotNull
  public static List<SchemaNamespaceDecl> findNamespaces(@NotNull Project project, @Nullable String namePrefix, @NotNull GlobalSearchScope searchScope) {
    // TODO cache all namespaces (if prefix is null)

    SchemaNamespaceByNameIndex index = SchemaNamespaceByNameIndex.EP_NAME.findExtension(SchemaNamespaceByNameIndex.class);

    final List<SchemaNamespaceDecl> result = new ArrayList<>();

    index.processAllKeys(project, namespaceFqn -> {
      if (namePrefix == null || namespaceFqn.startsWith(namePrefix)) {
        result.addAll(index.get(namespaceFqn, project, searchScope));
      }
      return true;
    });

    return result;
  }

  @Nullable
  public static SchemaNamespaceDecl findNamespace(@NotNull Project project, @NotNull Qn namespace, @NotNull GlobalSearchScope searchScope) {
    SchemaNamespaceByNameIndex index = SchemaNamespaceByNameIndex.EP_NAME.findExtension(SchemaNamespaceByNameIndex.class);

    Collection<SchemaNamespaceDecl> namespaceDecls = index.get(namespace.toString(), project, searchScope);
    return namespaceDecls.isEmpty() ? null : namespaceDecls.iterator().next();
  }

  @NotNull
  public static List<SchemaSupplementDef> findSupplementsBySource(@NotNull Project project, @NotNull SchemaTypeDef source) {
    GlobalSearchScope allScope = GlobalSearchScope.allScope(project);

    String name = source.getName();
    if (name == null) return Collections.emptyList();

    SchemaSupplementBySourceIndex index = SchemaSupplementBySourceIndex.EP_NAME.findExtension(SchemaSupplementBySourceIndex.class);

    final List<SchemaSupplementDef> result = new ArrayList<>();

    index.processAllKeys(project, sourceShortName -> {
      Collection<SchemaSupplementDef> schemaSupplementDefs = index.get(sourceShortName, project, allScope);
      for (SchemaSupplementDef schemaSupplementDef : schemaSupplementDefs) {
        ProgressManager.checkCanceled();
        SchemaTypeDef s = schemaSupplementDef.source();
        GlobalSearchScope supplementScope = SchemaSearchScopeUtil.getSearchScope(schemaSupplementDef);
        if (source == s && SchemaSearchScopeUtil.isInScope(supplementScope, source)) result.add(schemaSupplementDef);
      }
      return true;
    });

    return result;
  }

  @NotNull
  public static List<SchemaSupplementDef> findSupplementsBySupplemented(@NotNull Project project, @NotNull SchemaTypeDef supplemented) {
    GlobalSearchScope allScope = GlobalSearchScope.allScope(project);

    String name = supplemented.getName();
    if (name == null) return Collections.emptyList();

    SchemaSupplementBySupplementedIndex index = SchemaSupplementBySupplementedIndex.EP_NAME.findExtension(SchemaSupplementBySupplementedIndex.class);

    final List<SchemaSupplementDef> result = new ArrayList<>();

    index.processAllKeys(project, sourceShortName -> {
      Collection<SchemaSupplementDef> schemaSupplementDefs = index.get(sourceShortName, project, allScope);
      // check all supplement defs
      for (SchemaSupplementDef schemaSupplementDef : schemaSupplementDefs) {
        ProgressManager.checkCanceled();
        // supplemented must be visible by supplement
        GlobalSearchScope supplementScope = SchemaSearchScopeUtil.getSearchScope(schemaSupplementDef);
        if (SchemaSearchScopeUtil.isInScope(supplementScope, supplemented)) {
          // check their supplemented lists
          List<SchemaTypeDef> ss = schemaSupplementDef.supplemented();
          for (SchemaTypeDef s : ss) {
            // try to find `supplemented` among them
            if (supplemented == s) {
              result.add(schemaSupplementDef);
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
