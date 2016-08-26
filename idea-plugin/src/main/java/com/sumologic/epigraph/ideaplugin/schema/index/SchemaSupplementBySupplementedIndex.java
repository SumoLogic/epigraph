package com.sumologic.epigraph.ideaplugin.schema.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import io.epigraph.lang.parser.psi.EpigraphSupplementDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaSupplementBySupplementedIndex extends StringStubIndexExtension<EpigraphSupplementDef> {
  @NotNull
  @Override
  public StubIndexKey<String, EpigraphSupplementDef> getKey() {
    return SchemaStubIndexKeys.SUPPLEMENTS_BY_SUPPLEMENTED;
  }
}
