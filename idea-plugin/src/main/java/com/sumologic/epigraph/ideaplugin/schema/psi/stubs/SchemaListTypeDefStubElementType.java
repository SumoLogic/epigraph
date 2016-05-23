package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaListTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.impl.SchemaListTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaListTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaListTypeDefStub, SchemaListTypeDef> {
  public SchemaListTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "listtypedef");
  }

  @Override
  public SchemaListTypeDef createPsi(@NotNull SchemaListTypeDefStub stub) {
    return new SchemaListTypeDefImpl(stub, this);
  }

  @Override
  public SchemaListTypeDefStub createStub(@NotNull SchemaListTypeDef typeDef, StubElement parentStub) {
    return new SchemaListTypeDefStubImpl(parentStub, typeDef.getName(), typeDef.getNamespace());
  }

  @NotNull
  @Override
  protected SchemaListTypeDefStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub, String name, String namespace) throws IOException {
    return new SchemaListTypeDefStubImpl(parentStub, name, namespace);
  }
}
