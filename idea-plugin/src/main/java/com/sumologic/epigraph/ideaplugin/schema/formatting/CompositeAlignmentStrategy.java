package com.sumologic.epigraph.ideaplugin.schema.formatting;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.alignment.AlignmentStrategy;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class CompositeAlignmentStrategy extends AlignmentStrategy {
  private final AlignmentStrategy[] strategies;

  public CompositeAlignmentStrategy(AlignmentStrategy... strategies) {
    this.strategies = strategies;
  }

  @Nullable
  @Override
  public Alignment getAlignment(@Nullable IElementType parentType, @Nullable IElementType childType) {
    for (AlignmentStrategy strategy : strategies) {
      Alignment a = strategy.getAlignment(parentType, childType);
      if (a != null) {
        return a;
      }
    }
    return null;
  }
}
