package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.sumologic.epigraph.ideaplugin.schema.SchemaLanguage;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDefElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public abstract class SchemaTypeDefStubElementTypeBase<S extends SchemaTypeDefStubBase<T>, T extends SchemaTypeDefElement>
    extends IStubElementType<S, T> {

  private final String externalId;

  public SchemaTypeDefStubElementTypeBase(@NotNull @NonNls String debugName, String externalNameSuffix) {
    super(debugName, SchemaLanguage.INSTANCE);
    externalId = "epigraph_schema." + externalNameSuffix;
  }

  @NotNull
  @Override
  public String getExternalId() {
    return externalId;
  }

  @Override
  public void serialize(@NotNull S stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
  }

  protected static String getName(SchemaTypeDefElement typeDef) {
    return SchemaPresentationUtil.getName(typeDef, true);
  }

  @NotNull
  @Override
  public S deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return deserialize(dataStream, parentStub, deserializeName(dataStream));
  }

  @NotNull
  protected abstract S deserialize(@NotNull StubInputStream dataStream, StubElement parentStub, String name) throws IOException;

  private String deserializeName(@NotNull StubInputStream dataStream) throws IOException {
    return StringRef.toString(dataStream.readName());
  }

  @Override
  public void indexStub(@NotNull S stub, @NotNull IndexSink sink) {
     // TODO index by FQN?
  }
}

