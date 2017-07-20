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

import ws.epigraph.services._resources.epigraph.projections.output.datumtypeprojection._nt.recordtype.RecordTypeAssembler;
import ws.epigraph.services._resources.epigraph.projections.output.datumtypeprojection._nt.recordtype.declaredfields.Field_ListAssembler;
import ws.epigraph.services._resources.epigraph.projections.output.datumtypeprojection._nt.recordtype.supertypes.RecordType_ListAssembler;
import ws.epigraph.types.RecordTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class RecordTypeAssemblerImpl extends RecordTypeAssembler<RecordTypeApi> {
  public static final RecordTypeAssemblerImpl INSTANCE = new RecordTypeAssemblerImpl();

  private RecordTypeAssemblerImpl() {
    super(
        TypeAssemblerImpl.ABSTRACT_ASSEMBLER.on(t -> (RecordTypeApi) t), // abstract
        AnnotationsAssemblerImpl.INSTANCE.on(RecordTypeApi::annotations), // annotations
        new Field_ListAssembler<>(RecordTypeApi::immediateFields, FieldAssemblerImpl.INSTANCE), // declared fields
        DatumTypeAssemblerImpl.INSTANCE.on(RecordTypeApi::metaType), // meta
        QualifiedTypeNameAssemblerImpl.INSTANCE.on(RecordTypeApi::name), // name
        new RecordType_ListAssembler<>(RecordTypeApi::supertypes, INSTANCE) // supertypes
    );
  }
}
