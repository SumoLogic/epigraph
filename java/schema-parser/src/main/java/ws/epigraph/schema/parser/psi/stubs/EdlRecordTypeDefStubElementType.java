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

import com.intellij.psi.stubs.StubElement;
import ws.epigraph.schema.parser.psi.EdlRecordTypeDef;
import ws.epigraph.schema.parser.psi.impl.EdlRecordTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlRecordTypeDefStubElementType extends EdlTypeDefStubElementTypeBase<EdlRecordTypeDefStub, EdlRecordTypeDef> {
  public EdlRecordTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "recordtypedef");
  }

  @Override
  public EdlRecordTypeDef createPsi(@NotNull EdlRecordTypeDefStub stub) {
    return new EdlRecordTypeDefImpl(stub, this);
  }

  @Override
  public EdlRecordTypeDefStub createStub(@NotNull EdlRecordTypeDef typeDef, StubElement parentStub) {
    return new EdlRecordTypeDefStubImpl(parentStub);
  }
}