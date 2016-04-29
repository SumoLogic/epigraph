package com.sumologic.epigraph.ideaPlugin.schema.structureView;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.psi.PsiFile;
import com.sumologic.epigraph.ideaPlugin.schema.psi.SchemaFile;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaStructureViewFactory implements PsiStructureViewFactory {
  @Nullable
  @Override
  public StructureViewBuilder getStructureViewBuilder(PsiFile psiFile) {
    return new SchemaStructureViewBuilder((SchemaFile) psiFile);
  }
}
