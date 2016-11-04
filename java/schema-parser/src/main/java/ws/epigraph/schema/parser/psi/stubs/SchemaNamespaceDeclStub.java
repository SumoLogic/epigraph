package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.SchemaNamespaceDecl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface SchemaNamespaceDeclStub extends StubElement<SchemaNamespaceDecl> {
  Qn getFqn();
}
