package io.epigraph.lang.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.schema.parser.Fqn;
import io.epigraph.lang.lexer.EpigraphElementTypes;
import io.epigraph.lang.schema.parser.psi.SchemaNamespaceDecl;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaNamespaceDeclStubImpl extends StubBase<SchemaNamespaceDecl> implements SchemaNamespaceDeclStub {
  private final Fqn fqn;

  protected SchemaNamespaceDeclStubImpl(StubElement parent, Fqn fqn) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_NAMESPACE_DECL);
    this.fqn = fqn;
  }

  @Override
  public Fqn getFqn() {
    return fqn;
  }
}
