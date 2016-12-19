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

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import ws.epigraph.schema.parser.psi.SchemaFile;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaFileStubImpl extends PsiFileStubImpl<SchemaFile> implements SchemaFileStub {
  private final StringRef namespace;

  public SchemaFileStubImpl(SchemaFile file, StringRef namespace) {
    super(file);
    this.namespace = namespace;
  }

  @Override
  public String getNamespace() {
    return StringRef.toString(namespace);
  }

  @Override
  public IStubFileElementType getType() {
    return (IStubFileElementType) SchemaStubElementTypes.SCHEMA_FILE;
  }
}
