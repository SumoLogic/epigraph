package com.sumologic.epigraph.ideaplugin.schema.features.structureview;

import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import io.epigraph.lang.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaStructureViewBuilder extends TreeBasedStructureViewBuilder {
  private final SchemaFile schemaFile;

  public SchemaStructureViewBuilder(SchemaFile schemaFile) {
    this.schemaFile = schemaFile;
  }

  @NotNull
  @Override
  public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
    return new SchemaStructureViewModel(schemaFile, editor, new SchemaStructureViewElement(schemaFile))
        .withSorters(Sorter.ALPHA_SORTER)
        .withSuitableClasses(
            SchemaFile.class,
            EpigraphTypeDef.class,
            SchemaSupplementDef.class,
            SchemaFieldDecl.class,
            SchemaVarTagDecl.class,
            SchemaEnumMemberDecl.class,
            SchemaCustomParam.class
        );
  }
}
