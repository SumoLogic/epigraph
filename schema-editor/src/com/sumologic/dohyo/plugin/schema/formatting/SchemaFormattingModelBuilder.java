package com.sumologic.dohyo.plugin.schema.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.sumologic.dohyo.plugin.schema.SchemaLanguage;
import com.sumologic.dohyo.plugin.schema.parser.SchemaParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.*;

/**
 * Todo add doc
 *
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
    SpacingBuilder spacingBuilder = new SpacingBuilder(settings, SchemaLanguage.INSTANCE);

    for (IElementType type : SchemaParserDefinition.KEYWORDS.getTypes()) {
      if (type != S_LIST && type != S_MAP)
        spacingBuilder.before(type).spaces(1);
    }

    // TODO this should be configurable
    spacingBuilder.between(S_ID, S_CURLY_LEFT).spaces(1);
    spacingBuilder.before(S_COLON).spaces(0);
    spacingBuilder.after(S_COLON).spaces(1);
    spacingBuilder.around(S_EQ).spaces(1);
    spacingBuilder.after(S_COMMA).spaces(1);
    spacingBuilder.around(S_PLUS).spaces(1);
    spacingBuilder.after(S_BRACKET_RIGHT).spaces(1);
    spacingBuilder.after(S_DEFAULT).spaces(1);
    spacingBuilder.after(S_TYPE_REF).spaces(1);

    return spacingBuilder;
  }

  @Nullable
  @Override
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    return null;
  }
}
