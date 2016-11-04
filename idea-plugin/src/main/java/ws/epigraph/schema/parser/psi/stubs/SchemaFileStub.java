package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.PsiFileStub;
import ws.epigraph.schema.parser.psi.SchemaFile;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface SchemaFileStub extends PsiFileStub<SchemaFile> {
  String getNamespace();
}
