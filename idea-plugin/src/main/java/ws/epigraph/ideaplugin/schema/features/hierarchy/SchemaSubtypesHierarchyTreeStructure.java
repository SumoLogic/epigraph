package ws.epigraph.ideaplugin.schema.features.hierarchy;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.openapi.project.Project;
import ws.epigraph.ideaplugin.schema.brains.hierarchy.HierarchyCache;
import io.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
class SchemaSubtypesHierarchyTreeStructure extends HierarchyTreeStructure {
  SchemaSubtypesHierarchyTreeStructure(Project project, SchemaTypeDef typeDef) {
    super(project, new SchemaHierarchyNodeDescriptor(project, null, typeDef, true));
  }

  @NotNull
  @Override
  protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor descriptor) {
    final SchemaTypeDef typeDef = ((SchemaHierarchyNodeDescriptor) descriptor).getTypeDef();

//    final List<SchemaTypeDef> inheritors = new ArrayList<>(SchemaDirectTypeInheritorsSearch.search(typeDef).findAll());
    final List<SchemaTypeDef> inheritors = HierarchyCache.getHierarchyCache(myProject).getDirectTypeInheritors(typeDef);

    return inheritors.stream()
        .map(def -> new SchemaHierarchyNodeDescriptor(myProject, descriptor, def, false))
        .toArray();
  }
}
