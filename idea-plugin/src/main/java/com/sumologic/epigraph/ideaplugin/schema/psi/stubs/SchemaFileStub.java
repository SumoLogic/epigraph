package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.PsiFileStub;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFile;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaFileStub extends PsiFileStub<SchemaFile> {
  String getNamespace();
}
