package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.*;
import com.sumologic.epigraph.ideaplugin.schema.SchemaLanguage;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public abstract class SchemaTypeDefStubElementTypeBase<S extends SchemaTypeDefStubBase<T>, T extends SchemaTypeDef>
    extends IStubElementType<S, T> {

  private final String externalId;

  public SchemaTypeDefStubElementTypeBase(@NotNull @NonNls String debugName, String externalNameSuffix) {
    super(debugName, SchemaLanguage.INSTANCE);
    externalId = "epigraph_schema." + externalNameSuffix;
  }

  @NotNull
  @Override
  public S deserialize(@NotNull StubInputStream stubInputStream, StubElement stubElement) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void indexStub(@NotNull S s, @NotNull IndexSink indexSink) {

  }

  @Override
  public void serialize(@NotNull S s, @NotNull StubOutputStream stubOutputStream) throws IOException {

  }

  @NotNull
  @Override
  public String getExternalId() {
    return externalId;
  }

}

