package com.sumologic.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaStubIndexKeys;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.SchemaLanguage;
import com.sumologic.epigraph.schema.parser.psi.SchemaNamespaceDecl;
import com.sumologic.epigraph.schema.parser.psi.impl.SchemaNamespaceDeclImpl;
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
    return new SchemaNamespaceDeclStubImpl(parentStub, namespaceDecl.getFqn2());
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_schema.namespace";
  }

  @Override
  public void serialize(@NotNull SchemaNamespaceDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    Fqn fqn = stub.getFqn();
    dataStream.writeName(fqn == null ? null : fqn.toString());
  }

  @NotNull
  @Override
  public SchemaNamespaceDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    StringRef fqnStr = dataStream.readName();
    Fqn fqn = fqnStr == null ? null : Fqn.fromDotSeparated(fqnStr.getString());

    return new SchemaNamespaceDeclStubImpl(parentStub, fqn);
  }

  @Override
  public void indexStub(@NotNull SchemaNamespaceDeclStub stub, @NotNull IndexSink sink) {
    Fqn fqn = stub.getFqn();
    if (fqn != null) sink.occurrence(SchemaStubIndexKeys.NAMESPACE_BY_NAME, fqn.toString());
  }
}
