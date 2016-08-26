package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.PsiFileStub;
import io.epigraph.lang.parser.psi.SchemaFile;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface EpigraphFileStub extends PsiFileStub<SchemaFile> {
  String getNamespace();
}
