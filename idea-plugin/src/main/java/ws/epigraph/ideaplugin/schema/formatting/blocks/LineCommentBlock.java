package ws.epigraph.ideaplugin.schema.formatting.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import ws.epigraph.ideaplugin.schema.formatting.SchemaBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public class LineCommentBlock extends SchemaBlock {
  public LineCommentBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable Indent indent, SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment, indent, spacingBuilder);
  }

  @Override
  public Indent getIndent() {
    // This is a special case - comment block that is located at the very start of the line. We don't reformat such a blocks

    ASTNode previous = myNode.getTreePrev();

    // logic taken from AbstractJavaBlock: 783

    CharSequence prevChars;
    if (previous != null && previous.getElementType() == TokenType.WHITE_SPACE && (prevChars = previous.getChars()).length() > 0
        && prevChars.charAt(prevChars.length() - 1) == '\n') {
      return Indent.getNoneIndent();
    }

    return super.getIndent();
  }
}
