package ws.epigraph.ideaplugin.schema.formatting.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import ws.epigraph.ideaplugin.schema.formatting.SchemaBlock;
import ws.epigraph.schema.parser.SchemaParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ws.epigraph.schema.lexer.SchemaElementTypes.S_ANNOTATION;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TypeMemberDeclBlock extends SchemaBlock {
  public TypeMemberDeclBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable Indent indent, SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment, indent, spacingBuilder);
  }

  @Override
  protected Indent getChildIndent(ASTNode child) {
    IElementType type = child.getElementType();

    if (child.getPsi().getPrevSibling() == null) return Indent.getNoneIndent();
    if (SchemaParserDefinition.CURLY_BRACES.contains(type)) return Indent.getNoneIndent();
    if (type == S_ANNOTATION) return Indent.getNormalIndent();
    return Indent.getContinuationIndent();
  }

  @Nullable
  @Override
  protected Indent getChildIndent() {
    return Indent.getNormalIndent();
  }
}
