package com.sumologic.epigraph.ideaplugin.schema.features.hierarchy;

import com.intellij.ide.hierarchy.HierarchyBrowser;
import com.intellij.ide.hierarchy.HierarchyProvider;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import io.epigraph.schema.parser.psi.SchemaSupplementDef;
import io.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaHierarchyProvider implements HierarchyProvider {
  @Nullable
  @Override
  public PsiElement getTarget(@NotNull DataContext dataContext) {
    final Project project = CommonDataKeys.PROJECT.getData(dataContext);
    if (project == null) return null;

    final PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(dataContext);

    SchemaTypeDef typeDef = PsiTreeUtil.getParentOfType(element, SchemaTypeDef.class, false);
    if (typeDef != null) return typeDef;


    SchemaSupplementDef supplementDef = PsiTreeUtil.getParentOfType(element, SchemaSupplementDef.class, false);
    if (supplementDef != null) return supplementDef;

    return null;
  }

  @NotNull
  @Override
  public HierarchyBrowser createHierarchyBrowser(PsiElement target) {
    return new SchemaHierarchyBrowser(target.getProject(), target);
  }

  @Override
  public void browserActivated(@NotNull HierarchyBrowser hierarchyBrowser) {
    ((SchemaHierarchyBrowser) hierarchyBrowser).changeView(SchemaHierarchyBrowser.SUBTYPES_HIERARCHY_TYPE);
  }
}
