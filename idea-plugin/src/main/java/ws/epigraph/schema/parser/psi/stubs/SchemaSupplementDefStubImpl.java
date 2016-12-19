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

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import ws.epigraph.schema.lexer.SchemaElementTypes;
import ws.epigraph.schema.parser.psi.SchemaSupplementDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaSupplementDefStubImpl extends StubBase<SchemaSupplementDef> implements SchemaSupplementDefStub {
  private final SerializedFqnTypeRef sourceTypeRef;
  private final List<SerializedFqnTypeRef> supplementedTypeRefs;

  protected SchemaSupplementDefStubImpl(StubElement parent, SerializedFqnTypeRef sourceTypeRef, List<SerializedFqnTypeRef> supplementedTypeRefs) {
    super(parent, (IStubElementType) SchemaElementTypes.S_SUPPLEMENT_DEF);
    this.sourceTypeRef = sourceTypeRef;
    this.supplementedTypeRefs = supplementedTypeRefs;
  }

  @Override
  public SerializedFqnTypeRef getSourceTypeRef() {
    return sourceTypeRef;
  }

  @Nullable
  @Override
  public List<SerializedFqnTypeRef> getSupplementedTypeRefs() {
    return supplementedTypeRefs;
  }
}
