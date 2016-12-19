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
import ws.epigraph.schema.parser.SchemaLanguage;
import ws.epigraph.schema.parser.psi.SchemaNamespaceDecl;
import ws.epigraph.schema.parser.psi.impl.SchemaNamespaceDeclImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaNamespaceDeclStubElementType extends IStubElementType<SchemaNamespaceDeclStub, SchemaNamespaceDecl> {
  public SchemaNamespaceDeclStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, SchemaLanguage.INSTANCE);
  }

  @Override
  public SchemaNamespaceDecl createPsi(@NotNull SchemaNamespaceDeclStub stub) {
    return new SchemaNamespaceDeclImpl(stub, this);
  }

  @Override
  public SchemaNamespaceDeclStub createStub(@NotNull SchemaNamespaceDecl namespaceDecl, StubElement parentStub) {
    return new SchemaNamespaceDeclStubImpl(parentStub, namespaceDecl.getFqn());
  }

  @Override
  public @NotNull String getExternalId() {
    return "epigraph_schema.namespace";
  }

  @Override
  public void serialize(@NotNull SchemaNamespaceDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @Override
  public @NotNull SchemaNamespaceDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void indexStub(@NotNull SchemaNamespaceDeclStub stub, @NotNull IndexSink sink) {
  }
}
