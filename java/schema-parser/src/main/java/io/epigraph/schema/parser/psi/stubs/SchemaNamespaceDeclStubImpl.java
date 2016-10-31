package io.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.Qn;
import io.epigraph.schema.lexer.SchemaElementTypes;
import io.epigraph.schema.parser.psi.SchemaNamespaceDecl;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaNamespaceDeclStubImpl extends StubBase<SchemaNamespaceDecl> implements SchemaNamespaceDeclStub {
  private final Qn fqn;

  protected SchemaNamespaceDeclStubImpl(StubElement parent, Qn fqn) {
    super(parent, (IStubElementType) SchemaElementTypes.S_NAMESPACE_DECL);
    this.fqn = fqn;
  }

  @Override
  public Qn getFqn() {
    return fqn;
  }
}
