package com.sumologic.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.*;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaStubIndexKeys;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.SchemaLanguage;
import com.sumologic.epigraph.schema.parser.psi.SchemaSupplementDef;
import com.sumologic.epigraph.schema.parser.psi.impl.SchemaSupplementDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
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
        SerializedFqnTypeRef.fromFqnTypeRef(supplementDef.sourceRef()),
        supplementDef.supplementedRefs().stream()
            .map(SerializedFqnTypeRef::fromFqnTypeRef)
            .filter(i -> i != null) // filter out non-fqn or badly broken type refs
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
      Fqn shortName = sourceTypeRef.getShortName();
      if (shortName != null && shortName.getLast() != null) {
        sink.occurrence(SchemaStubIndexKeys.SUPPLEMENTS_BY_SOURCE, shortName.getLast());
      }
    }

    List<SerializedFqnTypeRef> supplementedTypeRefs = stub.getSupplementedTypeRefs();
    if (supplementedTypeRefs != null) {
      for (SerializedFqnTypeRef supplementedTypeRef : supplementedTypeRefs) {
        Fqn shortName = supplementedTypeRef.getShortName();
        if (shortName != null && shortName.getLast() != null) {
          sink.occurrence(SchemaStubIndexKeys.SUPPLEMENTS_BY_SUPPLEMENTED, shortName.getLast());
        }
      }
    }
  }
}
