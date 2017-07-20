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

import ws.epigraph.services._resources.epigraph.projections.output.datumtype._nt.recordtype.RecordTypeAsm;
import ws.epigraph.services._resources.epigraph.projections.output.datumtype._nt.recordtype.declaredfields.Field_ListAsm;
import ws.epigraph.services._resources.epigraph.projections.output.datumtype._nt.recordtype.supertypes.RecordType_ListAsm;
import ws.epigraph.types.RecordTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class RecordTypeAsmImpl extends RecordTypeAsm<RecordTypeApi> {
  public static final RecordTypeAsmImpl INSTANCE = new RecordTypeAsmImpl();

  private RecordTypeAsmImpl() {
    super(
        TypeAsmImpl.ABSTRACT_ASM.on(t -> (RecordTypeApi) t), // abstract
        AnnotationsAsmImpl.INSTANCE.on(RecordTypeApi::annotations), // annotations
        new Field_ListAsm<>(RecordTypeApi::immediateFields, FieldAsmImpl.INSTANCE), // declared fields
        DatumTypeAsmImpl.INSTANCE.on(RecordTypeApi::metaType), // meta
        QualifiedTypeNameAsmImpl.INSTANCE.on(RecordTypeApi::name), // name
        new RecordType_ListAsm<>(RecordTypeApi::supertypes, INSTANCE) // supertypes
    );
  }
}
