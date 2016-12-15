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

package ws.epigraph.ideaplugin.edl.formatting;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import ws.epigraph.ideaplugin.edl.formatting.blocks.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static ws.epigraph.edl.lexer.EdlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
interface EdlBlockFactory {
  Map<IElementType, EdlBlockFactory> factories =
      new ContainerUtil.ImmutableMapBuilder<IElementType, EdlBlockFactory>()
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

//          .put(E_ANNOTATION, CustomParamBlock::new)

          .put(E_COMMENT, LineCommentBlock::new)

          .build();

  EdlBlock create(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable Indent indent, SpacingBuilder spacingBuilder);

  static EdlBlock createBlock(
      ASTNode node,
      Wrap wrap,
      Alignment alignment,
      Indent indent,
      SpacingBuilder spacingBuilder
  ) {
    EdlBlockFactory factory = factories.get(node.getElementType());
    if (factory == null) {
      factory = EdlBlock::new;
    }

    return factory.create(
        node, wrap, alignment, indent, spacingBuilder
    );
  }
}