package com.sumologic.epigraph.ideaplugin.schema.features.imports;

import com.intellij.lang.ImportOptimizer;
import com.intellij.psi.PsiFile;
import com.sumologic.epigraph.ideaplugin.schema.brains.ImportsManager;
import com.sumologic.epigraph.schema.parser.psi.SchemaFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaImportOptimizer implements ImportOptimizer {
  @Override
  public boolean supports(PsiFile file) {
    return file instanceof SchemaFile;
  }

  @NotNull
  @Override
  public Runnable processFile(PsiFile file) {
    return ImportsManager.buildImportOptimizer((SchemaFile) file);
  }
}
