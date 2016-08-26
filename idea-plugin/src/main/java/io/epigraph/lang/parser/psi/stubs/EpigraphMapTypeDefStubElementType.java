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
public class EpigraphMapTypeDefStubElementType extends EpigraphTypeDefStubElementTypeBase<EpigraphMapTypeDefStub, EpigraphMapTypeDef> {
  public EpigraphMapTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "maptypedef");
  }

  @Override
  public EpigraphMapTypeDef createPsi(@NotNull EpigraphMapTypeDefStub stub) {
    return new EpigraphMapTypeDefImpl(stub, this);
  }

  @Override
  public EpigraphMapTypeDefStub createStub(@NotNull EpigraphMapTypeDef typeDef, StubElement parentStub) {
    return new EpigraphMapTypeDefStubImpl(
        parentStub,
        typeDef.getName(),
        Fqn.toNullableString(typeDef.getNamespace()),
        getSerializedExtendsTypeRefs(typeDef));
  }

  @NotNull
  @Override
  protected EpigraphMapTypeDefStub deserialize(
      @NotNull StubInputStream dataStream,
      StubElement parentStub,
      String name, String namespace,
      @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) throws IOException {
    return new EpigraphMapTypeDefStubImpl(parentStub, name, namespace, extendsTypeRefs);
  }
}
