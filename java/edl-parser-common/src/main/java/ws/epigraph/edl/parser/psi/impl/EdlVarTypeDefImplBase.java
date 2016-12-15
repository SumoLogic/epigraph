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

package ws.epigraph.edl.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import ws.epigraph.edl.parser.psi.EdlTypeDef;
import ws.epigraph.edl.parser.psi.EdlVarTypeDef;
import ws.epigraph.edl.parser.psi.TypeKind;
import ws.epigraph.edl.parser.psi.stubs.EdlVarTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract public class EdlVarTypeDefImplBase extends EdlTypeDefImplBase<EdlVarTypeDefStub, EdlVarTypeDef> implements EdlTypeDef {
  EdlVarTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  EdlVarTypeDefImplBase(@NotNull EdlVarTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.VAR;
  }
}
