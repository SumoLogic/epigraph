package ws.epigraph.ideaplugin.schema.features.hierarchy;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import ws.epigraph.ideaplugin.schema.brains.hierarchy.HierarchyCache;
import io.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public class SchemaSupertypesHierarchyTreeStructure extends HierarchyTreeStructure {
  SchemaSupertypesHierarchyTreeStructure(Project project, SchemaTypeDef typeDef) {
    super(project, new SchemaHierarchyNodeDescriptor(project, null, typeDef, true));
  }

  @NotNull
  @Override
  protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor descriptor) {
    final PsiElement element = descriptor.getPsiElement();
    if (element instanceof SchemaTypeDef) {
      SchemaTypeDef typeDef = (SchemaTypeDef) element;
//      Collection<SchemaTypeDef> parents = SchemaDirectTypeParentsSearch.search(typeDef).findAll();
      Collection<SchemaTypeDef> parents = HierarchyCache.getHierarchyCache(myProject).getDirectTypeParents(typeDef);
      if (parents.isEmpty()) return ArrayUtil.EMPTY_OBJECT_ARRAY;

      return parents.stream()
          .map(def -> new SchemaHierarchyNodeDescriptor(myProject, descriptor, def, false))
          .toArray();
    }

    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }
}
