package com.sumologic.epigraph.ideaplugin.schema.highlighting;

import com.intellij.codeInsight.daemon.impl.HighlightRangeExtension;
import com.intellij.psi.PsiFile;
import com.sumologic.epigraph.schema.parser.psi.SchemaFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaHighlightRangeExtension implements HighlightRangeExtension {
  @Override
  public boolean isForceHighlightParents(@NotNull PsiFile file) {
    return file instanceof SchemaFile;
  }
}
