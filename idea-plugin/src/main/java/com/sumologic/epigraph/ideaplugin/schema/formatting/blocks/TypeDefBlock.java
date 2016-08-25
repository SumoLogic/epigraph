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

import static io.epigraph.lang.lexer.EpigraphElementTypes.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class TypeDefBlock extends SchemaBlock {
  private static final TokenSet noIndentTokenSet = TokenSet.create(
      E_RECORD_TYPE_BODY,
      E_ENUM_TYPE_BODY, E_VAR_TYPE_BODY,
      E_PRIMITIVE_TYPE_BODY, E_MAP_TYPE_BODY, E_LIST_TYPE_BODY,

      E_ABSTRACT, E_POLYMORPHIC,
      E_ID, E_RECORD, E_VARTYPE, E_ENUM,
      E_ANON_LIST, E_ANON_MAP,
      E_STRING_T, E_INTEGER_T, E_LONG_T, E_DOUBLE_T, E_BOOLEAN_T
  );

  private static final TokenSet continuationTokenSet =
      TokenSet.create(
          E_EXTENDS_DECL, E_META_DECL, E_SUPPLEMENTS_DECL
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
//            S_RECORD, E_RECORD_TYPE_BODY,
//            S_UNION, E_UNION_TYPE_BODY,
//            S_MULTI, E_MULTI_TYPE_BODY,
//            S_ENUM, E_ENUM_TYPE_BODY
//        ),
        AlignmentStrategy.wrap( // align additional decls with type name? Alternatively use continuation..
            Alignment.createAlignment(),
            false,
            E_NEW_TYPE_NAME, E_EXTENDS_DECL, E_META_DECL, E_RECORD_SUPPLEMENTS_DECL
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
