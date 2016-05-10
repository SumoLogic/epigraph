package com.sumologic.epigraph.ideaplugin.schema.formatting.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.sumologic.epigraph.ideaplugin.schema.formatting.SchemaBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.S_LIST;
import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.S_MAP;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class AnonCollectionBlock extends SchemaBlock {
  public AnonCollectionBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable Indent indent, SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment, indent, spacingBuilder);
  }

  @Override
  protected Indent getChildIndent(ASTNode child) {
    if (child.getElementType() == S_LIST || child.getElementType() == S_MAP)
      return Indent.getNoneIndent();

    return Indent.getContinuationIndent();
  }

  @Nullable
  @Override
  protected Indent getChildIndent() {
    return Indent.getContinuationIndent();
  }
}
