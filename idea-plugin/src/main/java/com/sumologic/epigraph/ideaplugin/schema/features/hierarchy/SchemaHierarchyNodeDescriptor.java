package com.sumologic.epigraph.ideaplugin.schema.features.hierarchy;

import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.util.CompositeAppearance;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.ui.LayeredIcon;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaHierarchyNodeDescriptor extends HierarchyNodeDescriptor {
  protected SchemaHierarchyNodeDescriptor(@NotNull Project project, NodeDescriptor parentDescriptor, @NotNull PsiElement element, boolean isBase) {
    super(project, parentDescriptor, element, isBase);
    assert element instanceof SchemaTypeDef;
  }

  public SchemaTypeDef getTypeDef() {
    return (SchemaTypeDef) getPsiElement();
  }

  public final boolean update() {
    boolean changes = super.update();

    PsiNamedElement element = (PsiNamedElement) getPsiElement();
    if (element == null) {
      final String invalidPrefix = IdeBundle.message("node.hierarchy.invalid");
      if (!myHighlightedText.getText().startsWith(invalidPrefix)) {
        myHighlightedText.getBeginning().addText(invalidPrefix, HierarchyNodeDescriptor.getInvalidPrefixAttributes());
      }
      return true;
    }

    if (changes && myIsBase) {
      final LayeredIcon icon = new LayeredIcon(2);
      icon.setIcon(getIcon(), 0);
      icon.setIcon(AllIcons.Hierarchy.Base, 1, -AllIcons.Hierarchy.Base.getIconWidth() / 2, 0);
      setIcon(icon);
    }

    final CompositeAppearance oldText = myHighlightedText;

    myHighlightedText = new CompositeAppearance();

    TextAttributes classNameAttributes = null;
    if (myColor != null) {
      classNameAttributes = new TextAttributes(myColor, null, null, null, Font.PLAIN);
    }

    myHighlightedText.getEnding().addText(SchemaPresentationUtil.getName(element, false), classNameAttributes);
    myHighlightedText.getEnding().addText(" (" + NamespaceManager.getNamespace(element) + ")", HierarchyNodeDescriptor.getPackageNameAttributes());

/*
    final PsiElement psiElement = getPsiClass();

    final CompositeAppearance oldText = myHighlightedText;

    myHighlightedText = new CompositeAppearance();

    TextAttributes classNameAttributes = null;
    if (myColor != null) {
      classNameAttributes = new TextAttributes(myColor, null, null, null, Font.PLAIN);
    }
    if (psiElement instanceof PsiClass) {
      myHighlightedText.getEnding().addText(ClassPresentationUtil.getNameForClass((PsiClass)psiElement, false), classNameAttributes);
      myHighlightedText.getEnding().addText(" (" + JavaHierarchyUtil.getPackageName((PsiClass)psiElement) + ")", HierarchyNodeDescriptor.getPackageNameAttributes());
    } else if (psiElement instanceof PsiFunctionalExpression) {
      myHighlightedText.getEnding().addText(ClassPresentationUtil.getFunctionalExpressionPresentation(((PsiFunctionalExpression)psiElement), false));
    }
*/

    myName = myHighlightedText.getText();
    if (!Comparing.equal(myHighlightedText, oldText)) {
      changes = true;
    }
    return changes;
  }
}
