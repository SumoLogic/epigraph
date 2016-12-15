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
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import ws.epigraph.edl.parser.psi.EdlTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class EdlTypeDefStubBaseImpl<T extends EdlTypeDef> extends StubBase<T> implements EdlTypeDefStubBase<T> {
  private final String name;
  private final String namespace;

  @Nullable
  private List<SerializedFqnTypeRef> extendsTypeRefs;

  EdlTypeDefStubBaseImpl(StubElement parent,
                            final IStubElementType elementType,
                            final String name,
                            final String namespace,
                            @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) {
    super(parent, elementType);
    this.name = name;
    this.namespace = namespace;
    this.extendsTypeRefs = extendsTypeRefs;
  }

  @Nullable
  public String getName() {
    return name;
  }

  @Nullable
  @Override
  public String getNamespace() {
    return namespace;
  }

  @Nullable
  @Override
  public List<SerializedFqnTypeRef> getExtendsTypeRefs() {
    return extendsTypeRefs;
  }
}