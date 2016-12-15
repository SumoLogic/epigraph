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

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import ws.epigraph.edl.parser.EdlLanguage;
import ws.epigraph.edl.parser.EdlParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ws.epigraph.edl.lexer.EdlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlFormattingModelBuilder implements FormattingModelBuilder {
  @NotNull
  @Override
  public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
    return FormattingModelProvider.createFormattingModelForPsiFile(
        element.getContainingFile(),
        new EdlBlock(element.getNode(),
            Wrap.createWrap(WrapType.NONE, false),
            null,
            Indent.getAbsoluteNoneIndent(),
            createSpaceBuilder(settings)),
        settings
    );
  }

  private SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
    SpacingBuilder spacingBuilder = new SpacingBuilder(settings, EdlLanguage.INSTANCE);
    spacingBuilder.between(S_ANON_LIST, S_QID).spaces(1);

    for (IElementType type : EdlParserDefinition.KEYWORDS.getTypes()) {
      if (type != S_LIST && type != S_MAP)
        spacingBuilder.after(type).spaces(1);
    }

    // TODO this should be configurable

    spacingBuilder.between(S_QID, S_CURLY_LEFT).spaces(1);

    spacingBuilder.before(S_COLON).spaces(0);
    spacingBuilder.after(S_COLON).spaces(1);

    spacingBuilder.around(S_EQ).spaces(1);
    spacingBuilder.around(S_SLASH).spaces(0);
    spacingBuilder.around(S_DOT).spaces(0);

    spacingBuilder.before(S_COMMA).spaces(0);
    // TODO doesn't work:  [1,2,3] => [1, 2, 3]
    spacingBuilder.after(S_COMMA).spaces(1);

    spacingBuilder.around(S_BRACKET_LEFT).spaces(0);
    spacingBuilder.before(S_BRACKET_RIGHT).spaces(0);

    spacingBuilder.around(S_PAREN_LEFT).spaces(0);
    spacingBuilder.before(S_PAREN_RIGHT).spaces(0);

    spacingBuilder.around(S_ANGLE_LEFT).spaces(0);
    spacingBuilder.before(S_ANGLE_RIGHT).spaces(0);

    spacingBuilder.between(S_ANON_LIST, S_QID).spaces(1);
    spacingBuilder.between(S_ANON_MAP, S_QID).spaces(1);

    spacingBuilder.after(S_DEFAULT).spaces(1);
    spacingBuilder.after(S_TYPE_REF).spaces(1);

    spacingBuilder.before(S_EXTENDS_DECL).spaces(1);
    spacingBuilder.before(S_META_DECL).spaces(1);
    spacingBuilder.before(S_VAR_TAG_DECL).spaces(1);
    spacingBuilder.before(S_SUPPLEMENTS_DECL).spaces(1);

    spacingBuilder.before(S_RECORD_TYPE_BODY).spaces(1);
    spacingBuilder.before(S_VAR_TYPE_BODY).spaces(1);
    spacingBuilder.before(S_ENUM_TYPE_BODY).spaces(1);
    spacingBuilder.before(S_MAP_TYPE_BODY).spaces(1);
    spacingBuilder.before(S_LIST_TYPE_BODY).spaces(1);
    spacingBuilder.before(S_PRIMITIVE_TYPE_BODY).spaces(1);

    // TODO doesn't work: [1  2     3] => [1 2 3]
    spacingBuilder.between(S_DATA_VALUE, S_DATA_VALUE).spaces(1);

    // same for fields/vartype members ?
    spacingBuilder.between(S_ENUM_MEMBER_DECL, S_ENUM_MEMBER_DECL).spaces(1);

    return spacingBuilder;
  }

  @Nullable
  @Override
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    return null;
  }
}
