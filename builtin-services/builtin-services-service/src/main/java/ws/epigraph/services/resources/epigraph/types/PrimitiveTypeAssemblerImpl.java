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

import ws.epigraph.services._resources.epigraph.projections.output.datumtypeprojection._nt.primitivetype.PrimitiveTypeAssembler;
import ws.epigraph.services._resources.epigraph.projections.output.datumtypeprojection._nt.primitivetype.supertypes.PrimitiveType_ListAssembler;
import ws.epigraph.types.PrimitiveTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PrimitiveTypeAssemblerImpl extends PrimitiveTypeAssembler<PrimitiveTypeApi> {
  public static final PrimitiveTypeAssemblerImpl INSTANCE = new PrimitiveTypeAssemblerImpl();

  private PrimitiveTypeAssemblerImpl() {
    super(
        TypeAssemblerImpl.ABSTRACT_ASSEMBLER.on(t -> (PrimitiveTypeApi) t), // abstract
        AnnotationsAssemblerImpl.INSTANCE.on(PrimitiveTypeApi::annotations), // annotations
        DatumTypeAssemblerImpl.INSTANCE.on(PrimitiveTypeApi::metaType), // meta
        QualifiedTypeNameAssemblerImpl.INSTANCE.on(PrimitiveTypeApi::name), // name
        new PrimitiveType_ListAssembler<>(PrimitiveTypeApi::supertypes, INSTANCE) // supertypes
    );

  }
}
