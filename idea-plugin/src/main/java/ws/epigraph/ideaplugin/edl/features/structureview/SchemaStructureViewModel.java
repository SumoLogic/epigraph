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

package ws.epigraph.ideaplugin.edl.features.structureview;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
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
