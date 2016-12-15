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

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import ws.epigraph.edl.lexer.EdlElementTypes;
import ws.epigraph.edl.parser.psi.EdlVarTypeDef;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlVarTypeDefStubImpl extends EdlTypeDefStubBaseImpl<EdlVarTypeDef> implements EdlVarTypeDefStub {
  EdlVarTypeDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) EdlElementTypes.E_VAR_TYPE_DEF);
  }
}
