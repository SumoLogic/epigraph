package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaRecordTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.impl.SchemaRecordTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaRecordTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaRecordTypeDefStub, SchemaRecordTypeDef> {
  public SchemaRecordTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "recordtypedef");
  }

  @Override
  public SchemaRecordTypeDef createPsi(@NotNull SchemaRecordTypeDefStub stub) {
    return new SchemaRecordTypeDefImpl(stub, this);
  }

  @Override
  public SchemaRecordTypeDefStub createStub(@NotNull SchemaRecordTypeDef typeDef, StubElement parentStub) {
    return new SchemaRecordTypeDefStubImpl(parentStub, typeDef.getName(), typeDef.getNamespace());
  }

  @NotNull
  @Override
  protected SchemaRecordTypeDefStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub, String name, String namespace) throws IOException {
    return new SchemaRecordTypeDefStubImpl(parentStub, name, namespace);
  }
}
