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

package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import ws.epigraph.ideaplugin.schema.index.EdlStubIndexKeys;
import ws.epigraph.schema.parser.EdlLanguage;
import ws.epigraph.schema.parser.psi.EdlExtendsDecl;
import ws.epigraph.schema.parser.psi.EdlQnTypeRef;
import ws.epigraph.schema.parser.psi.EdlTypeDef;
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
public abstract class EdlTypeDefStubElementTypeBase<S extends EdlTypeDefStubBase<T>, T extends EdlTypeDef>
    extends IStubElementType<S, T> {

  private final String externalId;

  public EdlTypeDefStubElementTypeBase(@NotNull @NonNls String debugName, String externalNameSuffix) {
    super(debugName, EdlLanguage.INSTANCE);
    externalId = "epigraph_edl." + externalNameSuffix;
  }

  @NotNull
  @Override
  public String getExternalId() {
    return externalId;
  }

  @Nullable
  protected static List<SerializedFqnTypeRef> getSerializedExtendsTypeRefs(@NotNull EdlTypeDef typeDef) {
    EdlExtendsDecl extendsDecl = typeDef.getExtendsDecl();
    if (extendsDecl == null) return null;
    List<EdlQnTypeRef> typeRefList = extendsDecl.getQnTypeRefList();
    if (typeRefList.isEmpty()) return Collections.emptyList();

    return getSerializedFqnTypeRefs(typeRefList);
  }

  @NotNull
  protected static List<SerializedFqnTypeRef> getSerializedFqnTypeRefs(@NotNull List<EdlQnTypeRef> typeRefs) {
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
      sink.occurrence(EdlStubIndexKeys.TYPE_SHORT_NAMES, name);

      if (namespace != null) {
        sink.occurrence(EdlStubIndexKeys.TYPE_FQN, namespace + '.' + name);
      }
    }

    if (namespace != null) {
      sink.occurrence(EdlStubIndexKeys.TYPES_BY_NAMESPACE, namespace);
    }
  }
}

