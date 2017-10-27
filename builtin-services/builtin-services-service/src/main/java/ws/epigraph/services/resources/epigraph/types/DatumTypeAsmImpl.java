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

import epigraph.schema.DatumType;
import ws.epigraph.assembly.Asm;
import ws.epigraph.assembly.LazyAsm;
import ws.epigraph.services._resources.epigraph.projections.output.datumtype.DatumTypeAsm;
import ws.epigraph.services._resources.epigraph.projections.output.datumtype.OutputDatumTypeProjection;
import ws.epigraph.services._resources.epigraph.projections.output.datumtype.supertypes.DatumType_ListAsm;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DatumTypeAsmImpl extends DatumTypeAsm<DatumTypeApi> {
  public static final Asm<DatumTypeApi, OutputDatumTypeProjection, DatumType.Value> INSTANCE =
      new LazyAsm<>(DatumTypeAsmImpl::new);

  public DatumTypeAsmImpl() {
    super(
        t -> {
          switch (t.kind()) {
            case RECORD:
              return epigraph.schema.RecordType.type;
            case MAP:
              return epigraph.schema.MapType.type;
            case LIST:
              return epigraph.schema.ListType.type;
            case PRIMITIVE:
              return epigraph.schema.PrimitiveType.type;
            default:
              throw new RuntimeException("unknown kind: " + t.kind());
          }
        },
        TypeAsmImpl.ABSTRACT_ASM.from(t -> (DatumTypeApi) t), // abstract
        AnnotationsAsmImpl.INSTANCE.from(DatumTypeApi::annotations), // annotations
        INSTANCE.from(DatumTypeApi::metaType), // meta
        TypeNameAsmImpl.INSTANCE.from(DatumTypeApi::name), // name
        new DatumType_ListAsm<>(DatumTypeApi::supertypes, INSTANCE), // supertypes
        //tails
        RecordTypeAsmImpl.INSTANCE.from(t -> (RecordTypeApi) t), // record
        MapTypeAsmImpl.INSTANCE.from(t -> (MapTypeApi) t), // map
        ListTypeAsmImpl.INSTANCE.from(t -> (ListTypeApi) t), // list
        PrimitiveTypeAsmImpl.INSTANCE.from(t -> (PrimitiveTypeApi) t) // primitive
    );
  }
}
