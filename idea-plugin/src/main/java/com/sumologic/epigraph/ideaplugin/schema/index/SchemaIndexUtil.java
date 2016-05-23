package com.sumologic.epigraph.ideaplugin.schema.index;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.sumologic.epigraph.ideaplugin.schema.SchemaFileType;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaIndexUtil {
  @NotNull
  public static List<SchemaTypeDef> findTypeDefs(Project project, @Nullable Collection<String> namespaces, @Nullable String shortName) {
    return findTypeDefs(project, namespaces, shortName, new AddAllProcessor<>());
  }

  @Nullable
  public static SchemaTypeDef findTypeDef(Project project, @NotNull Collection<String> namespaces, @NotNull String shortName) {
    return findTypeDefs(project, namespaces, shortName, new TakeFirstProcessor<>());
  }

  private static <R> R findTypeDefs(Project project, @Nullable Collection<String> namespaces, @Nullable String shortName, @NotNull Processor<SchemaTypeDef, R> processor) {
    GlobalSearchScope scope = GlobalSearchScope.allScope(project);

    if (namespaces != null) {
      if (shortName != null) {
        SchemaFullTypeNameIndex index = SchemaFullTypeNameIndex.EP_NAME.findExtension(SchemaFullTypeNameIndex.class);
        assert index != null;

        for (String namespace : namespaces) {
          String fqn = namespace + '.' + shortName;
          Collection<SchemaTypeDef> typeDefs = index.get(fqn, project, scope);
          if (!processor.addMore(typeDefs)) break;
        }
      } else {
        SchemaTypesByNamespaceIndex index = SchemaTypesByNamespaceIndex.EP_NAME.findExtension(SchemaTypesByNamespaceIndex.class);
        assert index != null;

        for (String namespace : namespaces) {
          Collection<SchemaTypeDef> typeDefs = index.get(namespace, project, scope);
          if (!processor.addMore(typeDefs)) break;
        }
      }
    } else {
      SchemaShortTypeNameIndex index = SchemaShortTypeNameIndex.EP_NAME.findExtension(SchemaShortTypeNameIndex.class);
      assert index != null;

      Collection<String> shortNames;

      if (shortName != null) {
        shortNames = Collections.singleton(shortName);
      } else {
        shortNames = index.getAllKeys(project);
      }

      for (String name : shortNames) {
        Collection<SchemaTypeDef> typeDefs = index.get(name, project, scope);
        if (!processor.addMore(typeDefs)) break;
      }
    }

    return processor.result();
  }

  @NotNull
  public static List<SchemaNamespaceDecl> findNamespaces(@NotNull Project project, @Nullable String namePrefix) {
    // TODO index

    List<SchemaNamespaceDecl> result = new ArrayList<>();

    Collection<VirtualFile> virtualFiles =
        FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SchemaFileType.INSTANCE, GlobalSearchScope.allScope(project));

    for (VirtualFile virtualFile : virtualFiles) {

      SchemaFile schemaFile = (SchemaFile) PsiManager.getInstance(project).findFile(virtualFile);
      if (schemaFile == null) continue;

      SchemaNamespaceDecl namespaceDecl = schemaFile.getNamespaceDecl();
      if (namespaceDecl == null) continue;

      SchemaFqn fqn = namespaceDecl.getFqn();
      if (fqn == null) continue;

      if (namePrefix != null) {
        String fqnText = fqn.getFqn().toString();
        if (!fqnText.startsWith(namePrefix)) continue;
      }

      result.add(namespaceDecl);
    }

    return result;
  }

  private interface Processor<T, R> {
    boolean addMore(Collection<T> items);

    R result();
  }

  private static class AddAllProcessor<T> implements Processor<T, List<T>> {
    private final ArrayList<T> result = new ArrayList<>();

    @Override
    public boolean addMore(Collection<T> items) {
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
    public boolean addMore(Collection<T> items) {
      if (items.isEmpty()) return true;
      result = items.iterator().next();
      return false;
    }

    @Override
    public T result() {
      return result;
    }
  }
}
