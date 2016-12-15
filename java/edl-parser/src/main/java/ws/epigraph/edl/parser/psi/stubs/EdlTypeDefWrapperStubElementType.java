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

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import ws.epigraph.edl.parser.EdlLanguage;
import ws.epigraph.edl.parser.psi.EdlTypeDefWrapper;
import ws.epigraph.edl.parser.psi.impl.EdlTypeDefWrapperImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlTypeDefWrapperStubElementType extends IStubElementType<EdlTypeDefWrapperStub, EdlTypeDefWrapper> {
  public EdlTypeDefWrapperStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, EdlLanguage.INSTANCE);
  }

  @Override
  public boolean shouldCreateStub(ASTNode node) {
    return false;
  }

  @Override
  public EdlTypeDefWrapper createPsi(@NotNull EdlTypeDefWrapperStub stub) {
    return new EdlTypeDefWrapperImpl(stub, this);
  }

  @Override
  public EdlTypeDefWrapperStub createStub(@NotNull EdlTypeDefWrapper typeDef, StubElement parentStub) {
    return new EdlTypeDefWrapperStubImpl(parentStub);
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_edl.typedef";
  }

  @Override
  public void serialize(@NotNull EdlTypeDefWrapperStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @NotNull
  @Override
  public EdlTypeDefWrapperStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new EdlTypeDefWrapperStubImpl(parentStub);
  }

  @Override
  public void indexStub(@NotNull EdlTypeDefWrapperStub stub, @NotNull IndexSink sink) {

  }
}
