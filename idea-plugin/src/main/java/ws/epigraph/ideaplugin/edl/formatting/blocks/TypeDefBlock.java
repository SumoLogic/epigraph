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

package ws.epigraph.ideaplugin.edl.formatting.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ws.epigraph.ideaplugin.edl.formatting.EdlBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ws.epigraph.edl.lexer.EdlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TypeDefBlock extends EdlBlock {
  private static final TokenSet noIndentTokenSet = TokenSet.create(
      E_RECORD_TYPE_BODY,
      E_ENUM_TYPE_BODY, E_VAR_TYPE_BODY,
      E_PRIMITIVE_TYPE_BODY, E_MAP_TYPE_BODY, E_LIST_TYPE_BODY,

      E_ABSTRACT,
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
//            E_RECORD, E_RECORD_TYPE_BODY,
//            E_UNION, E_UNION_TYPE_BODY,
//            E_MULTI, E_MULTI_TYPE_BODY,
//            E_ENUM, E_ENUM_TYPE_BODY
//        ),
        AlignmentStrategy.wrap( // align additional decls with type name? Alternatively use continuation..
            Alignment.createAlignment(),
            false,
            E_NEW_TYPE_NAME, E_EXTENDE_DECL, E_META_DECL, E_RECORD_SUPPLEMENTE_DECL
        )
//        ,
//        AlignmentStrategy.wrap( // align members
//            Alignment.createAlignment(),
//            false,
//            E_ANNOTATION,
//            E_MULTI_MEMBER_DECL,
//            E_FIELD_DECL,
//            E_TAG_DECL,
//            E_ENUM_MEMBER_DECL
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
