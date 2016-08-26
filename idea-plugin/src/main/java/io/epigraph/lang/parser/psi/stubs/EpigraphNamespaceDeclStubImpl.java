package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.lexer.EpigraphElementTypes;
import io.epigraph.lang.parser.psi.SchemaNamespaceDecl;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphNamespaceDeclStubImpl extends StubBase<SchemaNamespaceDecl> implements EpigraphNamespaceDeclStub {
  private final Fqn fqn;

  protected EpigraphNamespaceDeclStubImpl(StubElement parent, Fqn fqn) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_NAMESPACE_DECL);
    this.fqn = fqn;
  }

  @Override
  public Fqn getFqn() {
    return fqn;
  }
}
