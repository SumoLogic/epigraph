package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.parser.psi.EpigraphPrimitiveTypeDef;
import io.epigraph.lang.parser.psi.impl.EpigraphPrimitiveTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPrimitiveTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaPrimitiveTypeDefStub, EpigraphPrimitiveTypeDef> {
  public SchemaPrimitiveTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "primitivetypedef");
  }

  @Override
  public EpigraphPrimitiveTypeDef createPsi(@NotNull SchemaPrimitiveTypeDefStub stub) {
    return new EpigraphPrimitiveTypeDefImpl(stub, this);
  }

  @Override
  public SchemaPrimitiveTypeDefStub createStub(@NotNull EpigraphPrimitiveTypeDef typeDef, StubElement parentStub) {
    return new SchemaPrimitiveTypeDefStubImpl(
        parentStub,
        typeDef.getName(),
        Fqn.toNullableString(typeDef.getNamespace()),
        getSerializedExtendsTypeRefs(typeDef));
  }

  @NotNull
  @Override
  protected SchemaPrimitiveTypeDefStub deserialize(
      @NotNull StubInputStream dataStream,
      StubElement parentStub,
      String name, String namespace,
      @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) throws IOException {
    return new SchemaPrimitiveTypeDefStubImpl(parentStub, name, namespace, extendsTypeRefs);
  }
}
