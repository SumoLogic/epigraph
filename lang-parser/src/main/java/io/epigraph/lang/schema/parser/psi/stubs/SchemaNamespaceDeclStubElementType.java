package io.epigraph.lang.schema.parser.psi.stubs;

import com.intellij.psi.stubs.*;
import io.epigraph.lang.EpigraphLanguage;
import io.epigraph.lang.schema.parser.psi.SchemaNamespaceDecl;
import io.epigraph.lang.schema.parser.psi.impl.SchemaNamespaceDeclImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaNamespaceDeclStubElementType extends IStubElementType<SchemaNamespaceDeclStub, SchemaNamespaceDecl> {
  public SchemaNamespaceDeclStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, EpigraphLanguage.INSTANCE);
  }

  @Override
  public SchemaNamespaceDecl createPsi(@NotNull SchemaNamespaceDeclStub stub) {
    return new SchemaNamespaceDeclImpl(stub, this);
  }

  @Override
  public SchemaNamespaceDeclStub createStub(@NotNull SchemaNamespaceDecl namespaceDecl, StubElement parentStub) {
    return new SchemaNamespaceDeclStubImpl(parentStub, namespaceDecl.getFqn2());
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_schema.namespace";
  }

  @Override
  public void serialize(@NotNull SchemaNamespaceDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @NotNull
  @Override
  public SchemaNamespaceDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void indexStub(@NotNull SchemaNamespaceDeclStub stub, @NotNull IndexSink sink) {
  }
}
