/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.ideaplugin.edl.features.hierarchy;

import com.intellij.ide.hierarchy.HierarchyBrowserManager;
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.ide.hierarchy.TypeHierarchyBrowserBase;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import ws.epigraph.ideaplugin.edl.presentation.EdlPresentationUtil;
import ws.epigraph.edl.parser.psi.EdlTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Comparator;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlHierarchyBrowser extends TypeHierarchyBrowserBase {
  // TODO buttons to show inherited fields?
  private static final Logger LOG = Logger.getInstance(EdlHierarchyBrowser.class);

  public EdlHierarchyBrowser(Project project, PsiElement element) {
    super(project, element);
  }

  @Override
  protected boolean isInterface(PsiElement psiElement) {
    return true; // this disables "type hierarchy" and only leaves subtypes and supertypes hierarchies
  }

  @Override
  protected boolean canBeDeleted(PsiElement psiElement) {
    return false;
  }

  @Override
  protected String getQualifiedName(PsiElement psiElement) {
    if (psiElement instanceof PsiNamedElement) {
      PsiNamedElement namedElement = (PsiNamedElement) psiElement;
      return EdlPresentationUtil.getName(namedElement, true);
    } else return null;
  }

  @Nullable
  @Override
  protected PsiElement getElementFromDescriptor(@NotNull HierarchyNodeDescriptor descriptor) {
    return descriptor.getPsiElement();
  }

  @Override
  protected void createTrees(@NotNull Map<String, JTree> trees) {
    createTreeAndSetupCommonActions(trees, IdeActions.GROUP_TYPE_HIERARCHY_POPUP);
  }

  @Nullable
  @Override
  protected JPanel createLegendPanel() {
    return null;
  }

  @Override
  protected boolean isApplicableElement(@NotNull PsiElement element) {
    return element instanceof EdlTypeDef;
  }

  @Nullable
  @Override
  protected HierarchyTreeStructure createHierarchyTreeStructure(@NotNull String type, @NotNull PsiElement psiElement) {
    if (SUPERTYPES_HIERARCHY_TYPE.equals(type)) {
      return new EdlSupertypesHierarchyTreeStructure(myProject, (EdlTypeDef) psiElement);
    } else if (SUBTYPES_HIERARCHY_TYPE.equals(type)) {
      return new EdlSubtypesHierarchyTreeStructure(myProject, (EdlTypeDef) psiElement);
    } else {
      LOG.error("unexpected type: " + type);
      return null;
    }
  }

  @Nullable
  @Override
  protected Comparator<NodeDescriptor> getComparator() {
    //noinspection ConstantConditions
    if (HierarchyBrowserManager.getInstance(myProject).getState().SORT_ALPHABETICALLY) {
      return AlphaComparator.INSTANCE;
    } else {
      return SourceComparator.INSTANCE;
    }
  }

  private static class AlphaComparator implements Comparator<NodeDescriptor> {
    public static final AlphaComparator INSTANCE = new AlphaComparator();

    @Override
    public int compare(NodeDescriptor nodeDescriptor1, NodeDescriptor nodeDescriptor2) {
      int weight1 = nodeDescriptor1.getWeight();
      int weight2 = nodeDescriptor2.getWeight();
      if (weight1 != weight2) {
        return weight1 - weight2;
      }
      String s1 = nodeDescriptor1.toString();
      String s2 = nodeDescriptor2.toString();
      if (s1 == null) return s2 == null ? 0 : -1;
      if (s2 == null) return +1;
      return StringUtil.naturalCompare(s1, s2);
    }
  }

  private static class SourceComparator implements Comparator<NodeDescriptor> {
    public static final SourceComparator INSTANCE = new SourceComparator();

    @Override
    public int compare(NodeDescriptor nodeDescriptor1, NodeDescriptor nodeDescriptor2) {
      int index1 = nodeDescriptor1.getIndex();
      int index2 = nodeDescriptor2.getIndex();
      if (index1 == index2) return 0;
      return index1 < index2 ? -1 : +1;
    }
  }

}
