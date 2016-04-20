package com.sumologic.dohyo.plugin.schema.formatting;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import com.sumologic.dohyo.plugin.schema.formatting.blocks.TypeMemberDeclBlock;
import com.sumologic.dohyo.plugin.schema.formatting.blocks.TypeDefBodyBlock;
import com.sumologic.dohyo.plugin.schema.formatting.blocks.TypeDefBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.*;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaBlockFactory {
  Map<IElementType, SchemaBlockFactory> factories =
      new ContainerUtil.ImmutableMapBuilder<IElementType, SchemaBlockFactory>()
          .put(S_RECORD_TYPE_DEF, TypeDefBlock::new)
          .put(S_UNION_TYPE_DEF, TypeDefBlock::new)
          .put(S_MULTI_TYPE_DEF, TypeDefBlock::new)
          .put(S_ENUM_TYPE_DEF, TypeDefBlock::new)

          .put(S_RECORD_TYPE_BODY, TypeDefBodyBlock::new)
          .put(S_UNION_TYPE_BODY, TypeDefBodyBlock::new)
          .put(S_MULTI_TYPE_BODY, TypeDefBodyBlock::new)
          .put(S_ENUM_MEMBER_BODY, TypeDefBodyBlock::new)

          .put(S_FIELD_DECL, TypeMemberDeclBlock::new)
          .put(S_TAG_DECL, TypeMemberDeclBlock::new)
          .put(S_MULTI_MEMBER_DECL, TypeMemberDeclBlock::new)
          .put(S_ENUM_MEMBER_DECL, TypeMemberDeclBlock::new)
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
