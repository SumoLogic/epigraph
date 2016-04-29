package com.sumologic.epigraph.ideaPlugin.schema.stubs;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.sumologic.epigraph.ideaPlugin.schema.psi.SchemaFile;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFileStub extends PsiFileStubImpl<SchemaFile> {
  public SchemaFileStub(SchemaFile file) {
    super(file);
  }
}
