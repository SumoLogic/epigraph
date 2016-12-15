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

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import ws.epigraph.lang.Qn;
import ws.epigraph.edl.parser.psi.EdlMapTypeDef;
import ws.epigraph.edl.parser.psi.impl.EdlMapTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlMapTypeDefStubElementType extends EdlTypeDefStubElementTypeBase<EdlMapTypeDefStub, EdlMapTypeDef> {
  public EdlMapTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "maptypedef");
  }

  @Override
  public EdlMapTypeDef createPsi(@NotNull EdlMapTypeDefStub stub) {
    return new EdlMapTypeDefImpl(stub, this);
  }

  @Override
  public EdlMapTypeDefStub createStub(@NotNull EdlMapTypeDef typeDef, StubElement parentStub) {
    return new EdlMapTypeDefStubImpl(
        parentStub,
        typeDef.getName(),
        Qn.toNullableString(typeDef.getNamespace()),
        getSerializedExtendsTypeRefs(typeDef));
  }

  @NotNull
  @Override
  protected EdlMapTypeDefStub deserialize(
      @NotNull StubInputStream dataStream,
      StubElement parentStub,
      String name, String namespace,
      @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) throws IOException {
    return new EdlMapTypeDefStubImpl(parentStub, name, namespace, extendsTypeRefs);
  }
}
