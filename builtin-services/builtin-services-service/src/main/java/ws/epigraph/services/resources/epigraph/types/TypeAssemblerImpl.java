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

import ws.epigraph.assembly.Assembler;
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection.Type_Assembler;
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection.abstract_.OutputBooleanProjection;
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection.supertypes.Type_ListAssembler;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.EntityTypeApi;
import ws.epigraph.types.Type;
import ws.epigraph.types.TypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class TypeAssemblerImpl extends Type_Assembler<TypeApi> {
  public static final TypeAssemblerImpl INSTANCE = new TypeAssemblerImpl();

  public static final Assembler<TypeApi, OutputBooleanProjection, epigraph.Boolean.Value> ABSTRACT_ASSEMBLER =
      (t, p, c) -> epigraph.Boolean.create(/*t.isAbstract()*/ false).asValue(); // todo

  private TypeAssemblerImpl() {
    super(
        t -> (Type) t,  // type extractor
        TypeNameAssemblerImpl.INSTANCE.on(TypeApi::name), // name
        new Type_ListAssembler<>(TypeApi::supertypes, TypeAssemblerImpl.INSTANCE), // supertypes
        ABSTRACT_ASSEMBLER, // abstract
        AnnotationsAssemblerImpl.INSTANCE.on(TypeApi::annotations), // annotations
        //tails
        EntityTypeAssembler.INSTANCE.on(t -> (EntityTypeApi) t), // entity
        DatumTypeAssemblerImpl.INSTANCE.on(t -> (DatumTypeApi) t) // datum
    );
  }
}
