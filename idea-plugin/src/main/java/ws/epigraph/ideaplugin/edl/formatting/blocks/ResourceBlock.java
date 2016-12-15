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

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * TODO real formatting for service declarations
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ResourceBlock implements Block {
  @NotNull
  private final ASTNode node;

  public ResourceBlock(@NotNull final ASTNode node) {this.node = node;}

  @NotNull
  @Override
  public TextRange getTextRange() {
    return node.getTextRange();
  }

  @NotNull
  @Override
  public List<Block> getSubBlocks() {
    return Collections.emptyList();
  }

  @Nullable
  @Override
  public Wrap getWrap() {
    return null;
  }

  @Nullable
  @Override
  public Indent getIndent() {
    return null;
  }

  @Nullable
  @Override
  public Alignment getAlignment() {
    return null;
  }

  @Nullable
  @Override
  public Spacing getSpacing(@Nullable final Block child1, @NotNull final Block child2) {
    return null;
  }

  @NotNull
  @Override
  public ChildAttributes getChildAttributes(final int newChildIndex) {
    return new ChildAttributes(Indent.getNoneIndent(), null);
  }

  @Override
  public boolean isIncomplete() {
    return false;
  }

  @Override
  public boolean isLeaf() {
    return true;
  }
}
