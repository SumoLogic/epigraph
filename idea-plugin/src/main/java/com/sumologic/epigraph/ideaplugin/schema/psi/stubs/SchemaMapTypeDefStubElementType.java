package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaMapTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.impl.SchemaMapTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaMapTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaMapTypeDefStub, SchemaMapTypeDef> {
  public SchemaMapTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "maptypedef");
  }

  @Override
  public SchemaMapTypeDef createPsi(@NotNull SchemaMapTypeDefStub stub) {
    return new SchemaMapTypeDefImpl(stub, this);
  }

  @Override
  public SchemaMapTypeDefStub createStub(@NotNull SchemaMapTypeDef typeDef, StubElement parentStub) {
    return new SchemaMapTypeDefStubImpl(
        parentStub,
        typeDef.getName(),
        typeDef.getNamespace(),
        getSerializedExtendsTypeRefs(typeDef));
  }

  @NotNull
  @Override
  protected SchemaMapTypeDefStub deserialize(
      @NotNull StubInputStream dataStream,
      StubElement parentStub,
      String name, String namespace,
      @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) throws IOException {
    return new SchemaMapTypeDefStubImpl(parentStub, name, namespace, extendsTypeRefs);
  }
}