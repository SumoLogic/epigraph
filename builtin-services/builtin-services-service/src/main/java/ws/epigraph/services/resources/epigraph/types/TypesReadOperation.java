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

import epigraph.schema.NameString;
import epigraph.schema.NameString_Type_Map;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.services.resources.epigraph.operations.types.AbstractReadTypesOperation;
import ws.epigraph.services.resources.epigraph.operations.types.output.OutputEpigraphFieldProjection;
import ws.epigraph.services.resources.epigraph.operations.types.output.OutputNameString_Type_MapKeyProjection;
import ws.epigraph.services.resources.epigraph.operations.types.output.OutputNameString_Type_MapProjection;
import ws.epigraph.services.resources.epigraph.operations.types.path.EpigraphFieldPath;
import ws.epigraph.services.resources.epigraph.projections.output.typeprojection.OutputType_Projection;
import ws.epigraph.types.Type;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static ws.epigraph.services.resources.epigraph.types.TypeBuilder.buildType;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TypesReadOperation extends AbstractReadTypesOperation {
  private final @NotNull Map<String, ? extends Type> types;

  public TypesReadOperation(
      final @NotNull ReadOperationDeclaration declaration,
      @NotNull Map<String, ? extends Type> types) {
    super(declaration);
    this.types = new TreeMap<>(types); // make it sorted
  }

  @Override
  protected @NotNull CompletableFuture<NameString_Type_Map.Data> process(
      final @NotNull NameString_Type_Map.Builder.Data builder,
      final @NotNull EpigraphFieldPath path,
      final @NotNull OutputEpigraphFieldProjection projection) {

    final OutputNameString_Type_MapProjection typesMapProjection = projection.dataProjection();
    final OutputType_Projection typeProjection = typesMapProjection.itemsProjection();
    final List<OutputNameString_Type_MapKeyProjection> keys = typesMapProjection.keys();

    final List<String> typeNames;

    if (keys == null) {
      typeNames = new ArrayList<>(types.keySet());
      Collections.sort(typeNames);
    } else {
      typeNames = keys.stream().map(k -> k.value().getVal()).collect(Collectors.toList());
    }

    NameString_Type_Map.Builder typeMapBuilder = NameString_Type_Map.create();
    builder.set(typeMapBuilder);

    for (String typeName : typeNames) {
      NameString.Imm key = NameString.create(typeName).toImmutable();
      final Type type = types.get(typeName);

      if (type == null)
        typeMapBuilder.putError(key, new ErrorValue(404, "Can't find type '" + typeName + "'"));
      else
        typeMapBuilder.put(key, buildType(type, typeProjection, new TypeBuilder.Context()));
    }

    return CompletableFuture.completedFuture(builder);
  }

}
