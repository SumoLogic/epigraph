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
import ws.epigraph.edl.parser.SchemaLanguage;
import ws.epigraph.edl.parser.psi.SchemaTypeDefWrapper;
import ws.epigraph.edl.parser.psi.impl.SchemaTypeDefWrapperImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaTypeDefWrapperStubElementType extends IStubElementType<SchemaTypeDefWrapperStub, SchemaTypeDefWrapper> {
  public SchemaTypeDefWrapperStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, SchemaLanguage.INSTANCE);
  }

  @Override
  public boolean shouldCreateStub(ASTNode node) {
    return false;
  }

  @Override
  public SchemaTypeDefWrapper createPsi(@NotNull SchemaTypeDefWrapperStub stub) {
    return new SchemaTypeDefWrapperImpl(stub, this);
  }

  @Override
  public SchemaTypeDefWrapperStub createStub(@NotNull SchemaTypeDefWrapper typeDef, StubElement parentStub) {
    return new SchemaTypeDefWrapperStubImpl(parentStub);
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_schema.typedef";
  }

  @Override
  public void serialize(@NotNull SchemaTypeDefWrapperStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @NotNull
  @Override
  public SchemaTypeDefWrapperStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new SchemaTypeDefWrapperStubImpl(parentStub);
  }

  @Override
  public void indexStub(@NotNull SchemaTypeDefWrapperStub stub, @NotNull IndexSink sink) {

  }
}
