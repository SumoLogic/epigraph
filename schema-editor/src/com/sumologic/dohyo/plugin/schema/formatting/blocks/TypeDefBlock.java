package com.sumologic.dohyo.plugin.schema.formatting.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.alignment.AlignmentStrategy;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.sumologic.dohyo.plugin.schema.formatting.CompositeAlignmentStrategy;
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
public class TypeDefBlock extends SchemaBlock {
  private static final TokenSet noIndentTokenSet =
      TokenSet.orSet(
          TokenSet.create(
              S_ID, S_EXTENDS_DECL, S_META_DECL, S_RECORD_SUPPLEMENTS_DECL,
              S_RECORD, S_UNION, S_MULTI, S_ENUM
          ), SchemaParserDefinition.CURLY_BRACES);

  public TypeDefBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable Indent indent, SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment, indent, spacingBuilder);
  }

  @NotNull
  @Override
  protected AlignmentStrategy getChildrenAlignmentStrategy() {
    return new CompositeAlignmentStrategy(
//        AlignmentStrategy.wrap( // align type name with braces
//            Alignment.createAlignment(),
//            false,
//            S_RECORD, S_RECORD_TYPE_BODY,
//            S_UNION, S_UNION_TYPE_BODY,
//            S_MULTI, S_MULTI_TYPE_BODY,
//            S_ENUM, S_ENUM_TYPE_BODY
//        ),
        AlignmentStrategy.wrap( // align additional decls with type name
            Alignment.createAlignment(),
            false,
            S_ID, S_EXTENDS_DECL, S_META_DECL, S_RECORD_SUPPLEMENTS_DECL
        )
//        ,
//        AlignmentStrategy.wrap( // align members
//            Alignment.createAlignment(),
//            false,
//            S_CUSTOM_PARAM,
//            S_MULTI_MEMBER_DECL,
//            S_FIELD_DECL,
//            S_TAG_DECL,
//            S_ENUM_MEMBER_DECL
//        )
    );
  }

  @Override
  protected Indent getChildIndent(ASTNode child) {
    IElementType childElementType = child.getElementType();

    if (noIndentTokenSet.contains(childElementType)) {
      return Indent.getNoneIndent();
    }

    return Indent.getNormalIndent();
  }
}