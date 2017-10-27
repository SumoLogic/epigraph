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

import ws.epigraph.services._resources.epigraph.projections.output.datumtype._nt.primitivetype.PrimitiveTypeAsm;
import ws.epigraph.services._resources.epigraph.projections.output.datumtype._nt.primitivetype.supertypes.PrimitiveType_ListAsm;
import ws.epigraph.types.PrimitiveTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class PrimitiveTypeAsmImpl extends PrimitiveTypeAsm<PrimitiveTypeApi> {
  public static final PrimitiveTypeAsmImpl INSTANCE = new PrimitiveTypeAsmImpl();

  private PrimitiveTypeAsmImpl() {
    super(
        TypeAsmImpl.ABSTRACT_ASM.from(t -> (PrimitiveTypeApi) t), // abstract
        AnnotationsAsmImpl.INSTANCE.from(PrimitiveTypeApi::annotations), // annotations
        DatumTypeAsmImpl.INSTANCE.from(PrimitiveTypeApi::metaType), // meta
        QualifiedTypeNameAsmImpl.INSTANCE.from(PrimitiveTypeApi::name), // name
        new PrimitiveType_ListAsm<>(PrimitiveTypeApi::supertypes, INSTANCE) // supertypes
    );

  }
}
