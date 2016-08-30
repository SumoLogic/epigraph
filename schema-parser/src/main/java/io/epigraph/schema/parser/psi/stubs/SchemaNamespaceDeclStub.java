package io.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import io.epigraph.schema.parser.Fqn;
import io.epigraph.schema.parser.psi.SchemaNamespaceDecl;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaNamespaceDeclStub extends StubElement<SchemaNamespaceDecl> {
  Fqn getFqn();
}
