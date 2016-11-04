package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.SchemaEnumTypeDef;
import ws.epigraph.schema.parser.psi.impl.SchemaEnumTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaEnumTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaEnumTypeDefStub, SchemaEnumTypeDef> {
  public SchemaEnumTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "enumtypedef");
  }

  @Override
  public SchemaEnumTypeDef createPsi(@NotNull SchemaEnumTypeDefStub stub) {
    return new SchemaEnumTypeDefImpl(stub, this);
  }

  @Override
  public SchemaEnumTypeDefStub createStub(@NotNull SchemaEnumTypeDef typeDef, StubElement parentStub) {
    return new SchemaEnumTypeDefStubImpl(
        parentStub,
        typeDef.getName(),
        Qn.toNullableString(typeDef.getNamespace()),
        getSerializedExtendsTypeRefs(typeDef)
    );
  }

  @NotNull
  @Override
  protected SchemaEnumTypeDefStub deserialize(@NotNull StubInputStream dataStream,
                                              StubElement parentStub,
                                              String name,
                                              String namespace,
                                              @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) throws IOException {
    return new SchemaEnumTypeDefStubImpl(parentStub, name, namespace, extendsTypeRefs);
  }
}
