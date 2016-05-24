package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import com.sumologic.epigraph.ideaplugin.schema.brains.Fqn;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaNamespaceDecl;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaNamespaceDeclStub extends StubElement<SchemaNamespaceDecl> {
  Fqn getFqn();
}
