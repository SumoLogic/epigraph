package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.*;
import com.sumologic.epigraph.ideaplugin.schema.SchemaLanguage;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.impl.SchemaTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaTypeDefStubElementType extends IStubElementType<SchemaTypeDefStub, SchemaTypeDef> {
  public SchemaTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, SchemaLanguage.INSTANCE);
  }

  @Override
  public SchemaTypeDef createPsi(@NotNull SchemaTypeDefStub stub) {
    return new SchemaTypeDefImpl(stub, this);
  }

  @Override
  public SchemaTypeDefStub createStub(@NotNull SchemaTypeDef typeDef, StubElement parentStub) {
    return new SchemaTypeDefStubImpl(parentStub);
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_schema.typedef";
  }

  @Override
  public void serialize(@NotNull SchemaTypeDefStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @NotNull
  @Override
  public SchemaTypeDefStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new SchemaTypeDefStubImpl(parentStub);
  }

  @Override
  public void indexStub(@NotNull SchemaTypeDefStub stub, @NotNull IndexSink sink) {

  }
}
