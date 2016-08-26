package com.sumologic.epigraph.ideaplugin.schema.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaShortTypeNameIndex extends StringStubIndexExtension<EpigraphTypeDef> {
  @NotNull
  @Override
  public StubIndexKey<String, EpigraphTypeDef> getKey() {
    return SchemaStubIndexKeys.TYPE_SHORT_NAMES;
  }
}
