package com.sumologic.dohyo.plugin.schema.formatting.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.sumologic.dohyo.plugin.schema.formatting.SchemaBlock;
import com.sumologic.dohyo.plugin.schema.parser.SchemaParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.*;
/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class TypeMemberDeclBlock extends SchemaBlock {
  public TypeMemberDeclBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable Indent indent, SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment, indent, spacingBuilder);
  }

  @Override
  protected Indent getChildIndent(ASTNode child) {
    IElementType childElementType = child.getElementType();

    if (childElementType == S_CUSTOM_PARAM ) return Indent.getNormalIndent();

//    if (SchemaParserDefinition.CURLY_BRACES.contains(childElementType)) {
      return Indent.getNoneIndent();
//    }
//
//    return Indent.getNormalIndent();
  }

  @Nullable
  @Override
  protected Indent getChildIndent() {
    return Indent.getNormalIndent();
  }
}
