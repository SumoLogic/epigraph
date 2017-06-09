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

package ws.epigraph.ideaplugin.schema.formatting.blocks;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.ideaplugin.schema.formatting.SchemaBlock;

import java.util.Collections;
import java.util.List;

/**
 * TODO real formatting for transformer declarations
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TransformerBlock extends SchemaBlock {
  public TransformerBlock(
      @NotNull final ASTNode node,
      @Nullable final Wrap wrap,
      @Nullable final Alignment alignment,
      @Nullable final Indent indent, final SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment, indent, spacingBuilder);
  }

  @Override
  protected List<Block> buildChildren() {
    return Collections.emptyList();
  }

}
