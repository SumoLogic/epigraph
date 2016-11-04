package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.lexer.SchemaElementTypes;
import ws.epigraph.schema.parser.psi.SchemaNamespaceDecl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
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
