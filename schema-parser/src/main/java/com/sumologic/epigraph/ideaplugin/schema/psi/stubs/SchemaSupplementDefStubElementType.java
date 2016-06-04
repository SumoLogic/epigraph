package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.*;
import com.sumologic.epigraph.ideaplugin.schema.SchemaLanguage;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaSupplementDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.impl.SchemaSupplementDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaSupplementDefStubElementType extends IStubElementType<SchemaSupplementDefStub, SchemaSupplementDef> {
  public SchemaSupplementDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, SchemaLanguage.INSTANCE);
  }

  @Override
  public SchemaSupplementDef createPsi(@NotNull SchemaSupplementDefStub stub) {
    return new SchemaSupplementDefImpl(stub, this);
  }

  @Override
  public SchemaSupplementDefStub createStub(@NotNull SchemaSupplementDef supplementDef, StubElement parentStub) {
    return new SchemaSupplementDefStubImpl(parentStub);
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_schema.supplement";
  }

  @Override
  public void serialize(@NotNull SchemaSupplementDefStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @NotNull
  @Override
  public SchemaSupplementDefStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void indexStub(@NotNull SchemaSupplementDefStub stub, @NotNull IndexSink sink) {
  }
}
