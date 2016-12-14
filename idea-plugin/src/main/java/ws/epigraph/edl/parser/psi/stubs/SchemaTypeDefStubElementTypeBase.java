/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.edl.parser.psi.stubs;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import ws.epigraph.ideaplugin.edl.index.SchemaStubIndexKeys;
import ws.epigraph.edl.parser.SchemaLanguage;
import ws.epigraph.edl.parser.psi.SchemaExtendsDecl;
import ws.epigraph.edl.parser.psi.SchemaQnTypeRef;
import ws.epigraph.edl.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
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
    List<SchemaQnTypeRef> typeRefList = extendsDecl.getQnTypeRefList();
    if (typeRefList.isEmpty()) return Collections.emptyList();

    return getSerializedFqnTypeRefs(typeRefList);
  }

  @NotNull
  protected static List<SerializedFqnTypeRef> getSerializedFqnTypeRefs(@NotNull List<SchemaQnTypeRef> typeRefs) {
    return typeRefs.stream()
        .map(SerializedFqnTypeRef::new)
        .filter(Objects::nonNull) // filter out non-fqn or badly broken type refs
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

