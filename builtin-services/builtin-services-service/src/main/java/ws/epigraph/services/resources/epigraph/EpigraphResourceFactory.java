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

package ws.epigraph.services.resources.epigraph;

import epigraph.schema.NameString_Type_Map;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.services.resources.epigraph.types.TypesReadOperation;
import ws.epigraph.types.Type;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EpigraphResourceFactory extends AbstractEpigraphResourceFactory {
  private final @NotNull Map<String, ? extends Type> types;

  public EpigraphResourceFactory(final @NotNull Map<String, ? extends Type> types) {this.types = types;}

  @Override
  protected @NotNull ReadOperation<NameString_Type_Map.Data> constructTypesReadOperation(
      final @NotNull ReadOperationDeclaration operationDeclaration) throws ServiceInitializationException {

    return new TypesReadOperation(operationDeclaration, types);
  }
}
