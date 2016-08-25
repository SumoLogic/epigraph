package com.sumologic.epigraph.ideaplugin.schema.formatting;

import com.intellij.formatting.*;
import com.intellij.formatting.alignment.AlignmentStrategy;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import io.epigraph.lang.EpigraphLanguage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaBlock extends AbstractBlock implements BlockEx {

  private SpacingBuilder spacingBuilder;
  private Indent indent;

  protected SchemaBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment,
                        @Nullable Indent indent, SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment);
    this.indent = indent;
    this.spacingBuilder = spacingBuilder;
  }

  @Nullable
  @Override
  public Language getLanguage() {
    return EpigraphLanguage.INSTANCE;
  }

  @Override
  protected List<Block> buildChildren() {
    List<Block> blocks = new ArrayList<>();
    ASTNode child = myNode.getFirstChildNode();
//    ASTNode previousChild = null;

    AlignmentStrategy as = getChildrenAlignmentStrategy();
//    Alignment alignment = null;

    while (child != null) {
      IElementType childElementType = child.getElementType();

      Alignment alignment = as.getAlignment(myNode.getElementType(), childElementType);

      if (childElementType != TokenType.WHITE_SPACE && !FormatterUtil.containsWhiteSpacesOnly(child)) {
        Block block = SchemaBlockFactory.createBlock(
            child,
            Wrap.createWrap(WrapType.NONE, false), // TODO
            alignment,
            getChildIndent(child),
            spacingBuilder);

        blocks.add(block);
      }

      child = child.getTreeNext();
    }
    return blocks;
  }

  @NotNull
  protected AlignmentStrategy getChildrenAlignmentStrategy() {
    return AlignmentStrategy.getNullStrategy();
  }

  protected Indent getChildIndent(ASTNode child) {
    return Indent.getNoneIndent();
  }

  @Nullable
  @Override
  protected Indent getChildIndent() {
    return Indent.getNoneIndent();
  }

  @Override
  public Indent getIndent() {
    return indent;
  }

  @Nullable
  @Override
  public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
    return spacingBuilder.getSpacing(this, child1, child2);
  }

  @Override
  public boolean isLeaf() {
    return myNode.getFirstChildNode() == null;
  }

//  @NotNull
//  @Override
//  public ChildAttributes getChildAttributes(int newChildIndex) {
//    return new ChildAttributes(Indent.getNoneIndent(), null);
//  }

}
