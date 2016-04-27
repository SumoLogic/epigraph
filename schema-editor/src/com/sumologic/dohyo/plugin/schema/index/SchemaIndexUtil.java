package com.sumologic.dohyo.plugin.schema.index;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.sumologic.dohyo.plugin.schema.SchemaFileType;
import com.sumologic.dohyo.plugin.schema.psi.SchemaDefs;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFile;
import com.sumologic.dohyo.plugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO this is a temp class, should use real indices instead
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaIndexUtil {
  public static List<SchemaTypeDef> findTypeDefs(Project project, @Nullable String name, @Nullable Class<? extends PsiElement> kind) {
    List<SchemaTypeDef> result = new ArrayList<>();

    Collection<VirtualFile> virtualFiles =
        FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SchemaFileType.INSTANCE, GlobalSearchScope.allScope(project));

    for (VirtualFile virtualFile : virtualFiles) {
      SchemaFile schemaFile = (SchemaFile) PsiManager.getInstance(project).findFile(virtualFile);
      if (schemaFile != null) {
        SchemaDefs defs = schemaFile.getDefs();
        if (defs != null) {
          result.addAll(defs.getTypeDefList().stream()
              .filter(typeDef -> kind == null || kind.isInstance(typeDef))
              .filter(typeDef -> name == null || name.equals(typeDef.getId().getText())) // TODO use getName
              .collect(Collectors.toList()));
        }
      }
    }
    return result;
  }
}
