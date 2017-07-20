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

import ws.epigraph.services._resources.epigraph.projections.output.datumtypeprojection._nt.listtype.ListTypeAssembler;
import ws.epigraph.services._resources.epigraph.projections.output.datumtypeprojection._nt.listtype.supertypes.ListType_ListAssembler;
import ws.epigraph.types.ListTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ListTypeAssemblerImpl extends ListTypeAssembler<ListTypeApi> {
  public static final ListTypeAssemblerImpl INSTANCE = new ListTypeAssemblerImpl();

  private ListTypeAssemblerImpl() {
    super(
        TypeAssemblerImpl.ABSTRACT_ASSEMBLER.on(t -> (ListTypeApi) t), // abstract
        AnnotationsAssemblerImpl.INSTANCE.on(ListTypeApi::annotations), // annotations
        DatumTypeAssemblerImpl.INSTANCE.on(ListTypeApi::metaType), // meta
        TypeNameAssemblerImpl.INSTANCE.on(ListTypeApi::name), // name
        new ListType_ListAssembler<>(ListTypeApi::supertypes, INSTANCE), // supertypes
        DataTypeAssemblerImpl.INSTANCE.on(ListTypeApi::elementType) // element type
    );
  }
}
