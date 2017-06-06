/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.ideaplugin.schema.features.structureview;

import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import ws.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
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
            SchemaTypeDef.class,
            SchemaSupplementDef.class,
            SchemaFieldDecl.class,
            SchemaEntityTagDecl.class,
            SchemaEnumMemberDecl.class,
            SchemaAnnotation.class
        );
  }
}
