package com.sumologic.dohyo.plugin.schema.stubs;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFile;

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
