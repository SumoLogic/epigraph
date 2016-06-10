package com.sumologic.epigraph.ideaplugin.schema.features.navigation;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class GotoTypeContributor implements ChooseByNameContributor {
  @NotNull
  @Override
  public String[] getNames(Project project, boolean includeNonProjectItems) {
    // TODO take scope into account
    return SchemaIndexUtil.findTypeDefs(project, null, null).stream().map(SchemaTypeDef::getName).toArray(String[]::new);
  }

  @NotNull
  @Override
  public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
    // TODO take scope into account
    return SchemaIndexUtil.findTypeDefs(project, null, Fqn.fromDotSeparated(name)).stream().toArray(NavigationItem[]::new);
  }
}
