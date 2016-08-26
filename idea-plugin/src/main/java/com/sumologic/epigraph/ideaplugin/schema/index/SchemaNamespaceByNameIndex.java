package com.sumologic.epigraph.ideaplugin.schema.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import io.epigraph.lang.parser.psi.EpigraphNamespaceDecl;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaNamespaceByNameIndex extends StringStubIndexExtension<EpigraphNamespaceDecl> {
  @NotNull
  @Override
  public StubIndexKey<String, EpigraphNamespaceDecl> getKey() {
    return SchemaStubIndexKeys.NAMESPACE_BY_NAME;
  }
}
