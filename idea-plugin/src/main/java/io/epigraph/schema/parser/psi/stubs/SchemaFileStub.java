package io.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.PsiFileStub;
import io.epigraph.schema.parser.psi.SchemaFile;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaFileStub extends PsiFileStub<SchemaFile> {
  String getNamespace();
}
