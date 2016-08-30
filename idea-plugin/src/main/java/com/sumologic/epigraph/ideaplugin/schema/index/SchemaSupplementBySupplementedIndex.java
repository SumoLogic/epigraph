package com.sumologic.epigraph.ideaplugin.schema.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import io.epigraph.schema.parser.psi.SchemaSupplementDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaSupplementBySupplementedIndex extends StringStubIndexExtension<SchemaSupplementDef> {
  @NotNull
  @Override
  public StubIndexKey<String, SchemaSupplementDef> getKey() {
    return SchemaStubIndexKeys.SUPPLEMENTS_BY_SUPPLEMENTED;
  }
}
