package com.sumologic.epigraph.ideaplugin.schema.formatting.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.sumologic.epigraph.ideaplugin.schema.formatting.SchemaBlock;
import io.epigraph.lang.schema.parser.SchemaParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class TypeDefBodyBlock extends SchemaBlock {
  public TypeDefBodyBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable Indent indent, SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment, indent, spacingBuilder);
  }

  @Override
  protected Indent getChildIndent(ASTNode child) {
    IElementType childElementType = child.getElementType();

    if (SchemaParserDefinition.CURLY_BRACES.contains(childElementType)) {
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
