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

import epigraph.Boolean;
import ws.epigraph.assembly.Asm;
import ws.epigraph.services._resources.epigraph.projections.output.type.Type_Asm;
import ws.epigraph.services._resources.epigraph.projections.output.type.abstract_.OutputBooleanProjection;
import ws.epigraph.services._resources.epigraph.projections.output.type.supertypes.Type_ListAsm;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class TypeAsmImpl extends Type_Asm<TypeApi> {
  public static final Asm<TypeApi, OutputBooleanProjection, Boolean.Value> ABSTRACT_ASM =
      (t, p, c) -> epigraph.Boolean.create(/*t.isAbstract()*/ false).asValue(); // todo

  public static final TypeAsmImpl INSTANCE = new TypeAsmImpl();

  private TypeAsmImpl() {
    super(
        t -> (t.kind() == TypeKind.ENTITY) ? epigraph.schema.VarType.type : epigraph.schema.DatumType.type,  // (output) type extractor
        ABSTRACT_ASM, // abstract
        AnnotationsAsmImpl.INSTANCE.on(TypeApi::annotations), // annotations
        TypeNameAsmImpl.INSTANCE.on(TypeApi::name), // name
        new Type_ListAsm<>(TypeApi::supertypes, TypeAsmImpl.INSTANCE), // supertypes
        //tails
        EntityTypeAsm.INSTANCE.on(t -> (EntityTypeApi) t), // entity
        DatumTypeAsmImpl.INSTANCE.on(t -> (DatumTypeApi) t) // datum
    );
  }
}
