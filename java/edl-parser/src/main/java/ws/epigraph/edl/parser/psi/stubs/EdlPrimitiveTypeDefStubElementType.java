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
import ws.epigraph.edl.parser.psi.EdlPrimitiveTypeDef;
import ws.epigraph.edl.parser.psi.impl.EdlPrimitiveTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlPrimitiveTypeDefStubElementType extends EdlTypeDefStubElementTypeBase<EdlPrimitiveTypeDefStub, EdlPrimitiveTypeDef> {
  public EdlPrimitiveTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "primitivetypedef");
  }

  @Override
  public EdlPrimitiveTypeDef createPsi(@NotNull EdlPrimitiveTypeDefStub stub) {
    return new EdlPrimitiveTypeDefImpl(stub, this);
  }

  @Override
  public EdlPrimitiveTypeDefStub createStub(@NotNull EdlPrimitiveTypeDef typeDef, StubElement parentStub) {
    return new EdlPrimitiveTypeDefStubImpl( parentStub);
  }

}
