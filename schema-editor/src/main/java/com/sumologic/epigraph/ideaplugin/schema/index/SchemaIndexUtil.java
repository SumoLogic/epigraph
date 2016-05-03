package com.sumologic.epigraph.ideaplugin.schema.index;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.sumologic.epigraph.ideaplugin.schema.SchemaFileType;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO this is a temp class, should use real indices instead
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaIndexUtil {
  @NotNull
  public static List<SchemaTypeDef> findTypeDefs(Project project, @NotNull Collection<String> namespaces, @Nullable String shortName) {
    List<SchemaTypeDef> result = new ArrayList<>();

    Collection<VirtualFile> virtualFiles =
        FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SchemaFileType.INSTANCE, GlobalSearchScope.allScope(project));

    for (VirtualFile virtualFile : virtualFiles) {

      SchemaFile schemaFile = (SchemaFile) PsiManager.getInstance(project).findFile(virtualFile);
      if (schemaFile == null) continue;

      SchemaDefs defs = schemaFile.getDefs();

      if (defs != null) {
        String namespaceFqnString = NamespaceManager.getNamespace(defs);
        if (namespaces.contains(namespaceFqnString)) {
          Stream<SchemaTypeDef> typeDefStream = defs.getTypeDefList().stream();
          result.addAll(typeDefStream
              .filter(typeDef -> shortName == null || shortName.equals(typeDef.getName()))
              .collect(Collectors.toList()));
        }
      }
    }

    return result;
  }

  @Nullable
  public static SchemaTypeDef findTypeDef(Project project, @NotNull Collection<String> namespaces, @NotNull String shortName) {
    Collection<VirtualFile> virtualFiles =
        FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SchemaFileType.INSTANCE, GlobalSearchScope.allScope(project));

    for (VirtualFile virtualFile : virtualFiles) {

      PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
      if (!(file instanceof SchemaFile)) continue;
      SchemaFile schemaFile = (SchemaFile) file;

      SchemaDefs defs = schemaFile.getDefs();

      if (defs != null) {
        String namespaceFqnString = NamespaceManager.getNamespace(defs);
        if (namespaces.contains(namespaceFqnString)) {
          Stream<SchemaTypeDef> typeDefStream = defs.getTypeDefList().stream();
          Optional<SchemaTypeDef> first = typeDefStream
              .filter(typeDef -> shortName.equals(typeDef.getName()))
              .findFirst();

          if (first.isPresent()) return first.get();
        }
      }
    }

    return null;
  }
}
