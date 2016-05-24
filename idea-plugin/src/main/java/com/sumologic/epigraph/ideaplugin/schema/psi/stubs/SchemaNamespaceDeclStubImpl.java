package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.sumologic.epigraph.ideaplugin.schema.brains.Fqn;
import com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaNamespaceDecl;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaNamespaceDeclStubImpl extends StubBase<SchemaNamespaceDecl> implements SchemaNamespaceDeclStub {
  private final Fqn fqn;

  protected SchemaNamespaceDeclStubImpl(StubElement parent, Fqn fqn) {
    super(parent, (IStubElementType) SchemaElementTypes.S_NAMESPACE_DECL);
    this.fqn = fqn;
  }

  @Override
  public Fqn getFqn() {
    return fqn;
  }
}
