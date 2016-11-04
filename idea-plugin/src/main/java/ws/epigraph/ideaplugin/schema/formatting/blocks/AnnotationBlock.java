package ws.epigraph.ideaplugin.schema.formatting.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import ws.epigraph.ideaplugin.schema.formatting.SchemaBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ws.epigraph.schema.lexer.SchemaElementTypes.S_ID;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class AnnotationBlock extends SchemaBlock {
  public AnnotationBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable Indent indent, SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment, indent, spacingBuilder);
  }

  @Override
  protected Indent getChildIndent(ASTNode child) {
    if (child.getElementType() == S_ID) return Indent.getNoneIndent();
    return Indent.getContinuationIndent();
  }

  @Nullable
  @Override
  protected Indent getChildIndent() {
    return Indent.getContinuationIndent();
  }
}
