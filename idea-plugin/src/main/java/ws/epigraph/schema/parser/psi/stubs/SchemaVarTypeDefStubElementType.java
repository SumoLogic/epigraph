package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.SchemaSupplementsDecl;
import ws.epigraph.schema.parser.psi.SchemaVarTypeDef;
import ws.epigraph.schema.parser.psi.impl.SchemaVarTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaVarTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaVarTypeDefStub, SchemaVarTypeDef> {
  public SchemaVarTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "vartypedef");
  }

  @Override
  public SchemaVarTypeDef createPsi(@NotNull SchemaVarTypeDefStub stub) {
    return new SchemaVarTypeDefImpl(stub, this);
  }

  @Override
  public SchemaVarTypeDefStub createStub(@NotNull SchemaVarTypeDef typeDef, StubElement parentStub) {
    SchemaSupplementsDecl supplementsDecl = typeDef.getSupplementsDecl();
    List<SerializedFqnTypeRef> supplementedRefs = supplementsDecl == null ? null :
        supplementsDecl.getQnTypeRefList().stream()
            .map(SerializedFqnTypeRef::new)
            .collect(Collectors.toList());

    return new SchemaVarTypeDefStubImpl(
        parentStub,
        typeDef.getName(),
        Qn.toNullableString(typeDef.getNamespace()),
        getSerializedExtendsTypeRefs(typeDef),
        supplementedRefs);
  }

  @Override
  public void serialize(@NotNull SchemaVarTypeDefStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    super.serialize(stub, dataStream);
    StubSerializerUtil.serializeCollection(stub.getSupplementedTypeRefs(), SerializedFqnTypeRef::serialize, dataStream);
  }

  @NotNull
  @Override
  protected SchemaVarTypeDefStub deserialize(
      @NotNull StubInputStream dataStream,
      StubElement parentStub,
      String name, String namespace,
      @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) throws IOException {
    List<SerializedFqnTypeRef> supplementedRefs = StubSerializerUtil.deserializeList(SerializedFqnTypeRef::deserialize, dataStream, true);
    return new SchemaVarTypeDefStubImpl(parentStub, name, namespace, extendsTypeRefs, supplementedRefs);
  }
}
