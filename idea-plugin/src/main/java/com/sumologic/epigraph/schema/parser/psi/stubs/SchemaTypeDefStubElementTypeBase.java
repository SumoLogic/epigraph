package com.sumologic.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaStubIndexKeys;
import com.sumologic.epigraph.schema.parser.SchemaLanguage;
import com.sumologic.epigraph.schema.parser.psi.SchemaExtendsDecl;
import com.sumologic.epigraph.schema.parser.psi.SchemaFqnTypeRef;
import com.sumologic.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public abstract class SchemaTypeDefStubElementTypeBase<S extends SchemaTypeDefStubBase<T>, T extends SchemaTypeDef>
    extends IStubElementType<S, T> {

  private final String externalId;

  public SchemaTypeDefStubElementTypeBase(@NotNull @NonNls String debugName, String externalNameSuffix) {
    super(debugName, SchemaLanguage.INSTANCE);
    externalId = "epigraph_schema." + externalNameSuffix;
  }

  @NotNull
  @Override
  public String getExternalId() {
    return externalId;
  }

  @Nullable
  protected static List<SerializedFqnTypeRef> getSerializedExtendsTypeRefs(@NotNull SchemaTypeDef typeDef) {
    SchemaExtendsDecl extendsDecl = typeDef.getExtendsDecl();
    if (extendsDecl == null) return null;
    List<SchemaFqnTypeRef> typeRefList = extendsDecl.getFqnTypeRefList();
    if (typeRefList.isEmpty()) return Collections.emptyList();

    return getSerializedFqnTypeRefs(typeRefList);
  }

  @NotNull
  protected static List<SerializedFqnTypeRef> getSerializedFqnTypeRefs(@NotNull List<SchemaFqnTypeRef> typeRefs) {
    return typeRefs.stream()
        .map(SerializedFqnTypeRef::new)
        .filter(i -> i != null) // filter out non-fqn or badly broken type refs
        .collect(Collectors.toList());
  }


  @Override
  public void serialize(@NotNull S stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeName(stub.getNamespace());

    List<SerializedFqnTypeRef> extendsTypeRefs = stub.getExtendsTypeRefs();
    StubSerializerUtil.serializeCollection(extendsTypeRefs, SerializedFqnTypeRef::serialize, dataStream);
  }

  @NotNull
  @Override
  public S deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    String name = deserializeName(dataStream);
    String namespace = deserializeName(dataStream);

    List<SerializedFqnTypeRef> extendsTypeRefs =
        StubSerializerUtil.deserializeList(SerializedFqnTypeRef::deserialize, dataStream, true);

    return deserialize(dataStream, parentStub, name, namespace, extendsTypeRefs);
  }

  @NotNull
  protected abstract S deserialize(@NotNull StubInputStream dataStream,
                                   StubElement parentStub,
                                   String name, String namespace,
                                   @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) throws IOException;

  private String deserializeName(@NotNull StubInputStream dataStream) throws IOException {
    return StringRef.toString(dataStream.readName());
  }

  @Override
  public void indexStub(@NotNull S stub, @NotNull IndexSink sink) {
    String name = stub.getName();
    String namespace = stub.getNamespace();

    if (name != null) {
      sink.occurrence(SchemaStubIndexKeys.TYPE_SHORT_NAMES, name);

      if (namespace != null) {
        sink.occurrence(SchemaStubIndexKeys.TYPE_FQN, namespace + '.' + name);
      }
    }

    if (namespace != null) {
      sink.occurrence(SchemaStubIndexKeys.TYPES_BY_NAMESPACE, namespace);
    }
  }
}

