package com.sumologic.epigraph.ideaplugin.schema.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import io.epigraph.lang.EpigraphLanguage;
import io.epigraph.lang.schema.parser.SchemaParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.epigraph.lang.lexer.EpigraphElementTypes.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFormattingModelBuilder implements FormattingModelBuilder {
  @NotNull
  @Override
  public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
    return FormattingModelProvider.createFormattingModelForPsiFile(
        element.getContainingFile(),
        new SchemaBlock(element.getNode(),
            Wrap.createWrap(WrapType.NONE, false),
            null,
            Indent.getAbsoluteNoneIndent(),
            createSpaceBuilder(settings)),
        settings
    );
  }

  private SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
    SpacingBuilder spacingBuilder = new SpacingBuilder(settings, EpigraphLanguage.INSTANCE);

    for (IElementType type : SchemaParserDefinition.KEYWORDS.getTypes()) {
      if (type != E_LIST && type != E_MAP)
        spacingBuilder.after(type).spaces(1);
    }

    // TODO this should be configurable

    spacingBuilder.between(E_QID, E_CURLY_LEFT).spaces(1);

    spacingBuilder.before(E_COLON).spaces(0);
    spacingBuilder.after(E_COLON).spaces(1);

    spacingBuilder.around(E_EQ).spaces(1);
    spacingBuilder.around(E_SLASH).spaces(0);
    spacingBuilder.around(E_DOT).spaces(0);

    spacingBuilder.before(E_COMMA).spaces(0);
    // TODO doesn't work:  [1,2,3] => [1, 2, 3]
    spacingBuilder.after(E_COMMA).spaces(1);

    spacingBuilder.around(E_BRACKET_LEFT).spaces(0);
    spacingBuilder.before(E_BRACKET_RIGHT).spaces(0);

    spacingBuilder.around(E_PAREN_LEFT).spaces(0);
    spacingBuilder.before(E_PAREN_RIGHT).spaces(0);

    spacingBuilder.around(E_ANGLE_LEFT).spaces(0);
    spacingBuilder.before(E_ANGLE_RIGHT).spaces(0);

    spacingBuilder.between(E_ANON_LIST, E_QID).spaces(1);
    spacingBuilder.between(E_ANON_MAP, E_QID).spaces(1);

    spacingBuilder.after(E_DEFAULT).spaces(1);
    spacingBuilder.after(E_TYPE_REF).spaces(1);

    spacingBuilder.before(E_EXTENDS_DECL).spaces(1);
    spacingBuilder.before(E_META_DECL).spaces(1);
    spacingBuilder.before(E_VAR_TAG_DECL).spaces(1);
    spacingBuilder.before(E_SUPPLEMENTS_DECL).spaces(1);

    spacingBuilder.before(E_RECORD_TYPE_BODY).spaces(1);
    spacingBuilder.before(E_VAR_TYPE_BODY).spaces(1);
    spacingBuilder.before(E_ENUM_TYPE_BODY).spaces(1);
    spacingBuilder.before(E_MAP_TYPE_BODY).spaces(1);
    spacingBuilder.before(E_LIST_TYPE_BODY).spaces(1);
    spacingBuilder.before(E_PRIMITIVE_TYPE_BODY).spaces(1);

    // TODO doesn't work: [1  2     3] => [1 2 3]
    spacingBuilder.between(E_DATA_VALUE, E_DATA_VALUE).spaces(1);

    // same for fields/vartype members ?
    spacingBuilder.between(E_ENUM_MEMBER_DECL, E_ENUM_MEMBER_DECL).spaces(1);

    return spacingBuilder;
  }

  @Nullable
  @Override
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    return null;
  }
}
