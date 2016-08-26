package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.parser.psi.SchemaNamespaceDecl;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface EpigraphNamespaceDeclStub extends StubElement<SchemaNamespaceDecl> {
  Fqn getFqn();
}
