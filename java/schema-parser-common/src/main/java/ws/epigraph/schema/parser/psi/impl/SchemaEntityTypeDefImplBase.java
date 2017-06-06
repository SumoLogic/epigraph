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

package ws.epigraph.schema.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.schema.parser.psi.SchemaEntityTypeDef;
import ws.epigraph.schema.parser.psi.TypeKind;
import ws.epigraph.schema.parser.psi.stubs.SchemaEntityTypeDefStub;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class SchemaEntityTypeDefImplBase extends SchemaTypeDefImplBase<SchemaEntityTypeDefStub, SchemaEntityTypeDef> {
  SchemaEntityTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  SchemaEntityTypeDefImplBase(@NotNull SchemaEntityTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @Override
  public @NotNull TypeKind getKind() {
    return TypeKind.VAR;
  }
}
