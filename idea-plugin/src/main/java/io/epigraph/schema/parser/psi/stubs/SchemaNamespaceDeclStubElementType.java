package io.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaStubIndexKeys;
import io.epigraph.lang.Qn;
import io.epigraph.schema.parser.SchemaLanguage;
import io.epigraph.schema.parser.psi.SchemaNamespaceDecl;
import io.epigraph.schema.parser.psi.impl.SchemaNamespaceDeclImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaNamespaceDeclStubElementType extends IStubElementType<SchemaNamespaceDeclStub, SchemaNamespaceDecl> {
  public SchemaNamespaceDeclStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, SchemaLanguage.INSTANCE);
  }

  @Override
  public SchemaNamespaceDecl createPsi(@NotNull SchemaNamespaceDeclStub stub) {
    return new SchemaNamespaceDeclImpl(stub, this);
  }

  @Override
  public SchemaNamespaceDeclStub createStub(@NotNull SchemaNamespaceDecl namespaceDecl, StubElement parentStub) {
    return new SchemaNamespaceDeclStubImpl(parentStub, namespaceDecl.getFqn());
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_schema.namespace";
  }

  @Override
  public void serialize(@NotNull SchemaNamespaceDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    Qn fqn = stub.getFqn();
    dataStream.writeName(fqn == null ? null : fqn.toString());
  }

  @NotNull
  @Override
  public SchemaNamespaceDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    StringRef fqnStr = dataStream.readName();
    Qn fqn = fqnStr == null ? null : Qn.fromDotSeparated(fqnStr.getString());

    return new SchemaNamespaceDeclStubImpl(parentStub, fqn);
  }

  @Override
  public void indexStub(@NotNull SchemaNamespaceDeclStub stub, @NotNull IndexSink sink) {
    Qn fqn = stub.getFqn();
    if (fqn != null) sink.occurrence(SchemaStubIndexKeys.NAMESPACE_BY_NAME, fqn.toString());
  }
}
