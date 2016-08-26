package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.parser.psi.EpigraphListTypeDef;
import io.epigraph.lang.parser.psi.impl.EpigraphListTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaListTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaListTypeDefStub, EpigraphListTypeDef> {
  public SchemaListTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "listtypedef");
  }

  @Override
  public EpigraphListTypeDef createPsi(@NotNull SchemaListTypeDefStub stub) {
    return new EpigraphListTypeDefImpl(stub, this);
  }

  @Override
  public SchemaListTypeDefStub createStub(@NotNull EpigraphListTypeDef typeDef, StubElement parentStub) {
    return new SchemaListTypeDefStubImpl(
        parentStub,
        typeDef.getName(),
        Fqn.toNullableString(typeDef.getNamespace()),
        getSerializedExtendsTypeRefs(typeDef));
  }

  @NotNull
  @Override
  protected SchemaListTypeDefStub deserialize(
      @NotNull StubInputStream dataStream,
      StubElement parentStub,
      String name, String namespace,
      @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) throws IOException {
    return new SchemaListTypeDefStubImpl(parentStub, name, namespace, extendsTypeRefs);
  }
}
