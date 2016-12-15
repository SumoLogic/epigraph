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

package ws.epigraph.ideaplugin.edl.formatting.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import ws.epigraph.ideaplugin.edl.formatting.EdlBlock;
import ws.epigraph.edl.parser.EdlParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TypeDefBodyBlock extends EdlBlock {
  public TypeDefBodyBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable Indent indent, SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment, indent, spacingBuilder);
  }

  @Override
  protected Indent getChildIndent(ASTNode child) {
    IElementType childElementType = child.getElementType();

    if (EdlParserDefinition.CURLY_BRACES.contains(childElementType)) {
      return Indent.getNoneIndent();
    }

    return Indent.getNormalIndent();
  }

  @Nullable
  @Override
  protected Indent getChildIndent() {
    return Indent.getNormalIndent();
  }
}
