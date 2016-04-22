package com.sumologic.dohyo.plugin.schema.structureView;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaStructureViewModel extends StructureViewModelBase
    implements StructureViewModel.ElementInfoProvider, StructureViewModel.ExpandInfoProvider {
  public SchemaStructureViewModel(@NotNull PsiFile psiFile, @Nullable Editor editor, @NotNull StructureViewTreeElement root) {
    super(psiFile, editor, root);
  }

  @Override
  public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
    return !isAlwaysLeaf(element);
  }

  @Override
  public boolean isAlwaysLeaf(StructureViewTreeElement element) {
    return ((SchemaStructureViewElement) element).isAlwaysLeaf();
  }

  @Override
  public boolean isAutoExpand(@NotNull StructureViewTreeElement element) {
    return ((SchemaStructureViewElement) element).isAutoExpand();
  }

  @Override
  public boolean isSmartExpand() {
    return false;
  }
}
