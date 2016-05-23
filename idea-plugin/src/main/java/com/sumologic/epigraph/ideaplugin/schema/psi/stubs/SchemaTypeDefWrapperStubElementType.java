package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.*;
import com.sumologic.epigraph.ideaplugin.schema.SchemaLanguage;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDefWrapper;
import com.sumologic.epigraph.ideaplugin.schema.psi.impl.SchemaTypeDefWrapperImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaTypeDefWrapperStubElementType extends IStubElementType<SchemaTypeDefWrapperStub, SchemaTypeDefWrapper> {
  public SchemaTypeDefWrapperStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, SchemaLanguage.INSTANCE);
  }

  @Override
  public SchemaTypeDefWrapper createPsi(@NotNull SchemaTypeDefWrapperStub stub) {
    return new SchemaTypeDefWrapperImpl(stub, this);
  }

  @Override
  public SchemaTypeDefWrapperStub createStub(@NotNull SchemaTypeDefWrapper typeDef, StubElement parentStub) {
    return new SchemaTypeDefWrapperStubImpl(parentStub);
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_schema.typedef";
  }

  @Override
  public void serialize(@NotNull SchemaTypeDefWrapperStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @NotNull
  @Override
  public SchemaTypeDefWrapperStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new SchemaTypeDefWrapperStubImpl(parentStub);
  }

  @Override
  public void indexStub(@NotNull SchemaTypeDefWrapperStub stub, @NotNull IndexSink sink) {

  }
}
