package com.sumologic.epigraph.ideaplugin.schema.features.navigation;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import io.epigraph.schema.parser.Fqn;
import io.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class GotoTypeContributor implements ChooseByNameContributor {
  @NotNull
  @Override
  public String[] getNames(Project project, boolean includeNonProjectItems) {
    GlobalSearchScope scope = includeNonProjectItems
        ? GlobalSearchScope.allScope(project)
        : GlobalSearchScope.projectScope(project);

    return SchemaIndexUtil.findTypeDefs(project, null, null, scope).stream().map(SchemaTypeDef::getName).toArray(String[]::new);
  }

  @NotNull
  @Override
  public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
    GlobalSearchScope scope = includeNonProjectItems
        ? GlobalSearchScope.allScope(project)
        : GlobalSearchScope.projectScope(project);

    return SchemaIndexUtil.findTypeDefs(project, null, Fqn.fromDotSeparated(name), scope).stream().toArray(NavigationItem[]::new);
  }
}
