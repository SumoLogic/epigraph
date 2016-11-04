package io.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.Qn;
import io.epigraph.schema.parser.psi.SchemaNamespaceDecl;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public interface SchemaNamespaceDeclStub extends StubElement<SchemaNamespaceDecl> {
  Qn getFqn();
}
