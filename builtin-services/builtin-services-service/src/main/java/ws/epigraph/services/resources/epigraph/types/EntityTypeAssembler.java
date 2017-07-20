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

import ws.epigraph.services._resources.epigraph.projections.output.typeprojection._nt.vartype.VarTypeAssembler;
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection._nt.vartype.supertypes.VarType_ListAssembler;
import ws.epigraph.services._resources.epigraph.projections.output.typeprojection._nt.vartype.tags.Tag_ListAssembler;
import ws.epigraph.types.EntityTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class EntityTypeAssembler extends VarTypeAssembler<EntityTypeApi> {
  public static final EntityTypeAssembler INSTANCE = new EntityTypeAssembler();

  private EntityTypeAssembler() {
    super(
        TypeAssemblerImpl.ABSTRACT_ASSEMBLER.on(t -> (EntityTypeApi) t), // abstract
        AnnotationsAssemblerImpl.INSTANCE.on(EntityTypeApi::annotations), // annotations
        QualifiedTypeNameAssemblerImpl.INSTANCE.on(EntityTypeApi::name), // name
        new VarType_ListAssembler<>(EntityTypeApi::supertypes, INSTANCE),  // supertypes
        new Tag_ListAssembler<>(EntityTypeApi::tags, TagAssemblerImpl.INSTANCE) // tags
    );
  }
}
