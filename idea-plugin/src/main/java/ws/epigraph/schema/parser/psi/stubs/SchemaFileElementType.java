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

import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import ws.epigraph.ideaplugin.schema.brains.NamespaceManager;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.SchemaLanguage;
import ws.epigraph.schema.parser.psi.SchemaFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaFileElementType extends IStubFileElementType<SchemaFileStub> {
  public SchemaFileElementType() {
    super("epigraph_schema.FILE", SchemaLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_schema.FILE";
  }

  @Override
  public int getStubVersion() {
    return 4; // advance every time indexes or serialized stubs change
  }

  @Override
  public StubBuilder getBuilder() {
    return new DefaultStubBuilder() {
      @NotNull
      @Override
      protected StubElement createStubForFile(@NotNull PsiFile file) {
        if (file instanceof SchemaFile) {
          SchemaFile schemaFile = (SchemaFile) file;
          Qn namespace = NamespaceManager.getNamespace(schemaFile);
          return new SchemaFileStubImpl(schemaFile, StringRef.fromNullableString(namespace == null ? null : namespace.toString()));
        } else return super.createStubForFile(file);
      }
    };
  }

  @Override
  public void serialize(@NotNull SchemaFileStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getNamespace());
  }

  @NotNull
  @Override
  public SchemaFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    StringRef namespace = dataStream.readName();
    return new SchemaFileStubImpl(null, namespace);
  }

  // indexStub by namespace?
}
