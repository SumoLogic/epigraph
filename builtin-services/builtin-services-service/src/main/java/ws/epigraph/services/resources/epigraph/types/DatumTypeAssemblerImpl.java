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

import ws.epigraph.services._resources.epigraph.projections.output.datumtypeprojection.DatumTypeAssembler;
import ws.epigraph.services._resources.epigraph.projections.output.datumtypeprojection.supertypes.DatumType_ListAssembler;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DatumTypeAssemblerImpl extends DatumTypeAssembler<DatumTypeApi> {
  public static final DatumTypeAssemblerImpl INSTANCE = new DatumTypeAssemblerImpl();

  public DatumTypeAssemblerImpl() {
    super(
        t -> (Type) t, // type extractor
        TypeAssemblerImpl.ABSTRACT_ASSEMBLER.on(t -> (DatumTypeApi) t), // abstract
        AnnotationsAssemblerImpl.INSTANCE.on(DatumTypeApi::annotations), // annotations
        INSTANCE.on(DatumTypeApi::metaType), // meta
        TypeNameAssemblerImpl.INSTANCE.on(DatumTypeApi::name), // name
        new DatumType_ListAssembler<>(DatumTypeApi::supertypes, INSTANCE), // supertypes
        //tails
        RecordTypeAssemblerImpl.INSTANCE.on(t -> (RecordTypeApi) t), // record
        MapTypeAssemblerImpl.INSTANCE.on(t -> (MapTypeApi) t), // map
        ListTypeAssemblerImpl.INSTANCE.on(t -> (ListTypeApi) t), // list
        PrimitiveTypeAssemblerImpl.INSTANCE.on(t -> (PrimitiveTypeApi) t) // primitive
    );
  }
}
