package ws.epigraph.ideaplugin.schema.formatting;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import ws.epigraph.ideaplugin.schema.formatting.blocks.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static io.epigraph.schema.lexer.SchemaElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
interface SchemaBlockFactory {
  Map<IElementType, SchemaBlockFactory> factories =
      new ContainerUtil.ImmutableMapBuilder<IElementType, SchemaBlockFactory>()
          .put(S_RECORD_TYPE_DEF, TypeDefBlock::new)
          .put(S_VAR_TYPE_DEF, TypeDefBlock::new)
          .put(S_ENUM_TYPE_DEF, TypeDefBlock::new)
          .put(S_PRIMITIVE_TYPE_DEF, TypeDefBlock::new)
          .put(S_LIST_TYPE_DEF, TypeDefBlock::new)
          .put(S_MAP_TYPE_DEF, TypeDefBlock::new)

          .put(S_VAR_TYPE_BODY, TypeDefBodyBlock::new)
          .put(S_RECORD_TYPE_BODY, TypeDefBodyBlock::new)
          .put(S_ENUM_TYPE_BODY, TypeDefBodyBlock::new)
          .put(S_PRIMITIVE_TYPE_BODY, TypeDefBodyBlock::new)

          .put(S_FIELD_DECL, TypeMemberDeclBlock::new)
          .put(S_VAR_TAG_DECL, TypeMemberDeclBlock::new)
          .put(S_ENUM_MEMBER_DECL, TypeMemberDeclBlock::new)

          .put(S_EXTENDS_DECL, ExtendsBlock::new)
          .put(S_META_DECL, MetaBlock::new)
          .put(S_SUPPLEMENTS_DECL, SupplementsBlock::new)

          .put(S_DEFAULT_OVERRIDE, DefaultOverrideBlock::new)

          .put(S_ANON_LIST, AnonCollectionBlock::new)
          .put(S_ANON_MAP, AnonCollectionBlock::new)

//          .put(S_ANNOTATION, CustomParamBlock::new)

          .put(S_COMMENT, LineCommentBlock::new)

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
