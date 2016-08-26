package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.parser.psi.EpigraphMapTypeDef;
import io.epigraph.lang.parser.psi.impl.EpigraphMapTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaMapTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaMapTypeDefStub, EpigraphMapTypeDef> {
  public SchemaMapTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "maptypedef");
  }

  @Override
  public EpigraphMapTypeDef createPsi(@NotNull SchemaMapTypeDefStub stub) {
    return new EpigraphMapTypeDefImpl(stub, this);
  }

  @Override
  public SchemaMapTypeDefStub createStub(@NotNull EpigraphMapTypeDef typeDef, StubElement parentStub) {
    return new SchemaMapTypeDefStubImpl(
        parentStub,
        typeDef.getName(),
        Fqn.toNullableString(typeDef.getNamespace()),
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
