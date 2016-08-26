package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.*;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaStubIndexKeys;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.schema.SchemaLanguage;
import io.epigraph.lang.parser.psi.SchemaSupplementDef;
import io.epigraph.lang.parser.psi.impl.SchemaSupplementDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaSupplementDefStubElementType extends IStubElementType<SchemaSupplementDefStub, SchemaSupplementDef> {
  public SchemaSupplementDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, SchemaLanguage.INSTANCE);
  }

  @Override
  public SchemaSupplementDef createPsi(@NotNull SchemaSupplementDefStub stub) {
    return new SchemaSupplementDefImpl(stub, this);
  }

  @Override
  public SchemaSupplementDefStub createStub(@NotNull SchemaSupplementDef supplementDef, StubElement parentStub) {
    return new SchemaSupplementDefStubImpl(parentStub,
        new SerializedFqnTypeRef(supplementDef.sourceRef()),
        supplementDef.supplementedRefs().stream()
            .map(SerializedFqnTypeRef::new)
            .filter(Objects::nonNull) // filter out non-fqn or badly broken type refs
            .collect(Collectors.toList())
    );
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_schema.supplement";
  }

  @Override
  public void serialize(@NotNull SchemaSupplementDefStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    SerializedFqnTypeRef.serializeNullable(stub.getSourceTypeRef(), dataStream);
    StubSerializerUtil.serializeCollection(stub.getSupplementedTypeRefs(), SerializedFqnTypeRef::serialize, dataStream);
  }

  @NotNull
  @Override
  public SchemaSupplementDefStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    SerializedFqnTypeRef target = SerializedFqnTypeRef.deserializeNullable(dataStream);
    List<SerializedFqnTypeRef> sources =
        StubSerializerUtil.deserializeList(SerializedFqnTypeRef::deserialize, dataStream, true);

    return new SchemaSupplementDefStubImpl(parentStub, target, sources);
  }

  @Override
  public void indexStub(@NotNull SchemaSupplementDefStub stub, @NotNull IndexSink sink) {
    SerializedFqnTypeRef sourceTypeRef = stub.getSourceTypeRef();
    if (sourceTypeRef != null) {
      Fqn ref = sourceTypeRef.getShortName();
      if (ref != null && ref.last() != null) {
        //noinspection ConstantConditions
        sink.occurrence(SchemaStubIndexKeys.SUPPLEMENTS_BY_SOURCE, ref.last());
      }
    }

    List<SerializedFqnTypeRef> supplementedTypeRefs = stub.getSupplementedTypeRefs();
    if (supplementedTypeRefs != null) {
      for (SerializedFqnTypeRef supplementedTypeRef : supplementedTypeRefs) {
        Fqn ref = supplementedTypeRef.getShortName();
        if (ref != null && ref.last() != null) {
          //noinspection ConstantConditions
          sink.occurrence(SchemaStubIndexKeys.SUPPLEMENTS_BY_SUPPLEMENTED, ref.last());
        }
      }
    }
  }
}
