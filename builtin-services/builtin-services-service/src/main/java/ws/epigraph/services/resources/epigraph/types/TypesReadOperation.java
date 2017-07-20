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

package ws.epigraph.services.resources.epigraph.types;

import epigraph.schema.NameString_Type_Map;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.assembly.AsmContext;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.services._resources.epigraph.operations.read.types.AbstractReadTypesOperation;
import ws.epigraph.services._resources.epigraph.operations.read.types.output.OutputEpigraphFieldProjection;
import ws.epigraph.services._resources.epigraph.operations.read.types.path.EpigraphFieldPath;
import ws.epigraph.types.TypeApi;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;


/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TypesReadOperation extends AbstractReadTypesOperation {
  private final @NotNull Map<String, ? extends TypeApi> types;

  public TypesReadOperation(
      @NotNull ReadOperationDeclaration declaration,
      @NotNull Map<String, ? extends TypeApi> types) {

    super(declaration);
    this.types = new TreeMap<>(types); // make it sorted
  }

  @Override
  protected @NotNull CompletableFuture<NameString_Type_Map.Data> process(
      final @NotNull NameString_Type_Map.Data.Builder builder,
      final @NotNull EpigraphFieldPath path,
      final @NotNull OutputEpigraphFieldProjection projection) {

    builder.set_(NameString_Type_MapAsmImpl.INSTANCE.assemble(
        types,
        projection.dataProjection(),
        new AsmContext()
    ));
    return CompletableFuture.completedFuture(builder);

  }

}
