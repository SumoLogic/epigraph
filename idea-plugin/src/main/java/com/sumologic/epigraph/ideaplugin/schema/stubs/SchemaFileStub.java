package com.sumologic.epigraph.ideaplugin.schema.stubs;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFile;

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
