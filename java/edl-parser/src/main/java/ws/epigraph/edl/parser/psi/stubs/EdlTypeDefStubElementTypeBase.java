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

import com.intellij.psi.stubs.*;
import ws.epigraph.edl.parser.EdlLanguage;
import ws.epigraph.edl.parser.psi.EdlTypeDef;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class EdlTypeDefStubElementTypeBase<S extends EdlTypeDefStubBase<T>, T extends EdlTypeDef>
    extends IStubElementType<S, T> {

  private final String externalId;

  protected EdlTypeDefStubElementTypeBase(@NotNull @NonNls String debugName, String externalNameSuffix) {
    super(debugName, EdlLanguage.INSTANCE);
    externalId = "epigraph_edl." + externalNameSuffix;
  }

  @NotNull
  @Override
  public S deserialize(@NotNull StubInputStream stubInputStream, StubElement stubElement) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void indexStub(@NotNull S s, @NotNull IndexSink indexSink) {

  }

  @Override
  public void serialize(@NotNull S s, @NotNull StubOutputStream stubOutputStream) throws IOException {

  }

  @NotNull
  @Override
  public String getExternalId() {
    return externalId;
  }

}

