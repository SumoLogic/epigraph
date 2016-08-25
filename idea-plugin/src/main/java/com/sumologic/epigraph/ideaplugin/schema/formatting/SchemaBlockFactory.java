package com.sumologic.epigraph.ideaplugin.schema.formatting;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import com.sumologic.epigraph.ideaplugin.schema.formatting.blocks.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static io.epigraph.lang.lexer.EpigraphElementTypes.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
interface SchemaBlockFactory {
  Map<IElementType, SchemaBlockFactory> factories =
      new ContainerUtil.ImmutableMapBuilder<IElementType, SchemaBlockFactory>()
          .put(E_RECORD_TYPE_DEF, TypeDefBlock::new)
          .put(E_VAR_TYPE_DEF, TypeDefBlock::new)
          .put(E_ENUM_TYPE_DEF, TypeDefBlock::new)
          .put(E_PRIMITIVE_TYPE_DEF, TypeDefBlock::new)
          .put(E_LIST_TYPE_DEF, TypeDefBlock::new)
          .put(E_MAP_TYPE_DEF, TypeDefBlock::new)

          .put(E_VAR_TYPE_BODY, TypeDefBodyBlock::new)
          .put(E_RECORD_TYPE_BODY, TypeDefBodyBlock::new)
          .put(E_ENUM_TYPE_BODY, TypeDefBodyBlock::new)
          .put(E_PRIMITIVE_TYPE_BODY, TypeDefBodyBlock::new)

          .put(E_FIELD_DECL, TypeMemberDeclBlock::new)
          .put(E_VAR_TAG_DECL, TypeMemberDeclBlock::new)
          .put(E_ENUM_MEMBER_DECL, TypeMemberDeclBlock::new)

          .put(E_EXTENDS_DECL, ExtendsBlock::new)
          .put(E_META_DECL, MetaBlock::new)
          .put(E_SUPPLEMENTS_DECL, SupplementsBlock::new)

          .put(E_DEFAULT_OVERRIDE, DefaultOverrideBlock::new)

          .put(E_ANON_LIST, AnonCollectionBlock::new)
          .put(E_ANON_MAP, AnonCollectionBlock::new)

//          .put(E_CUSTOM_PARAM, CustomParamBlock::new)

          .put(E_COMMENT, LineCommentBlock::new)

          .build();

  SchemaBlock create(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable Indent indent, SpacingBuilder spacingBuilder);

  static SchemaBlock createBlock(
      ASTNode node,
      Wrap wrap,
      Alignment alignment,
      Indent indent,
      SpacingBuilder spacingBuilder
  ) {
    SchemaBlockFactory factory = factories.get(node.getElementType());
    if (factory == null) {
      factory = SchemaBlock::new;
    }

    return factory.create(
        node, wrap, alignment, indent, spacingBuilder
    );
  }
}
