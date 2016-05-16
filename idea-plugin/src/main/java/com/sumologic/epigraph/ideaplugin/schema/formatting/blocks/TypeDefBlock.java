package com.sumologic.epigraph.ideaplugin.schema.formatting.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.sumologic.epigraph.ideaplugin.schema.formatting.SchemaBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class TypeDefBlock extends SchemaBlock {
  private static final TokenSet noIndentTokenSet = TokenSet.create(
      S_RECORD_TYPE_BODY,
      S_ENUM_TYPE_BODY, S_VAR_TYPE_BODY,
      S_PRIMITIVE_TYPE_BODY, S_MAP_TYPE_BODY, S_LIST_TYPE_BODY,

      S_ABSTRACT, S_POLYMORPHIC,
      S_ID, S_RECORD, S_VARTYPE, S_ENUM,
      S_PRIMITIVE_KIND, S_ANON_LIST, S_ANON_MAP
  );

  private static final TokenSet continuationTokenSet =
      TokenSet.create(
          S_EXTENDS_DECL, S_META_DECL, S_RECORD_SUPPLEMENTS_DECL
      );

  public TypeDefBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable Indent indent, SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment, indent, spacingBuilder);
  }

  /*
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
        AlignmentStrategy.wrap( // align additional decls with type name? Alternatively use continuation..
            Alignment.createAlignment(),
            false,
            S_NEW_TYPE_NAME, S_EXTENDS_DECL, S_META_DECL, S_RECORD_SUPPLEMENTS_DECL
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
  */

  @Override
  protected Indent getChildIndent(ASTNode child) {
    IElementType childElementType = child.getElementType();

    if (noIndentTokenSet.contains(childElementType)) {
      return Indent.getNoneIndent();
    }

    if (continuationTokenSet.contains(childElementType)) {
      return Indent.getContinuationIndent();
    }

    return Indent.getNormalIndent();
  }

  @Nullable
  @Override
  protected Indent getChildIndent() {
    return Indent.getNormalIndent();
  }
}
