/*
 * Copyright 2017 Sumo Logic
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

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.SchemaEntityTypeDef;
import ws.epigraph.schema.parser.psi.SchemaSupplementsDecl;
import ws.epigraph.schema.parser.psi.impl.SchemaEntityTypeDefImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaEntityTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaEntityTypeDefStub, SchemaEntityTypeDef> {
  public SchemaEntityTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "entitydef");
  }

  @Override
  public SchemaEntityTypeDef createPsi(@NotNull SchemaEntityTypeDefStub stub) {
    return new SchemaEntityTypeDefImpl(stub, this);
  }

  @Override
  public SchemaEntityTypeDefStub createStub(@NotNull SchemaEntityTypeDef typeDef, StubElement parentStub) {
    SchemaSupplementsDecl supplementsDecl = typeDef.getSupplementsDecl();
    List<SerializedFqnTypeRef> supplementedRefs = supplementsDecl == null ? null :
        supplementsDecl.getQnTypeRefList().stream()
            .map(SerializedFqnTypeRef::new)
            .collect(Collectors.toList());

    return new SchemaEntityTypeDefStubImpl(
        parentStub,
        typeDef.getName(),
        Qn.toNullableString(typeDef.getNamespace()),
        getSerializedExtendsTypeRefs(typeDef),
        supplementedRefs);
  }

  @Override
  public void serialize(@NotNull SchemaEntityTypeDefStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    super.serialize(stub, dataStream);
    StubSerializerUtil.serializeCollection(stub.getSupplementedTypeRefs(), SerializedFqnTypeRef::serialize, dataStream);
  }

  @NotNull
  @Override
  protected SchemaEntityTypeDefStub deserialize(
      @NotNull StubInputStream dataStream,
      StubElement parentStub,
      String name, String namespace,
      @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) throws IOException {
    List<SerializedFqnTypeRef> supplementedRefs = StubSerializerUtil.deserializeList(SerializedFqnTypeRef::deserialize, dataStream, true);
    return new SchemaEntityTypeDefStubImpl(parentStub, name, namespace, extendsTypeRefs, supplementedRefs);
  }
}
