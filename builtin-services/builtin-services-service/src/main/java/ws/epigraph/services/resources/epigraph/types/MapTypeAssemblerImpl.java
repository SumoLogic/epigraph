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

import ws.epigraph.services._resources.epigraph.projections.output.datumtypeprojection._nt.maptype.MapTypeAssembler;
import ws.epigraph.services._resources.epigraph.projections.output.datumtypeprojection._nt.maptype.supertypes.MapType_ListAssembler;
import ws.epigraph.types.MapTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class MapTypeAssemblerImpl extends MapTypeAssembler<MapTypeApi> {
  public static final MapTypeAssemblerImpl INSTANCE = new MapTypeAssemblerImpl();

  private MapTypeAssemblerImpl() {
    super(
        TypeAssemblerImpl.ABSTRACT_ASSEMBLER.on(t -> (MapTypeApi) t), // abstract
        AnnotationsAssemblerImpl.INSTANCE.on(MapTypeApi::annotations), // annotations
        DatumTypeAssemblerImpl.INSTANCE.on(MapTypeApi::keyType), // key type
        DatumTypeAssemblerImpl.INSTANCE.on(MapTypeApi::metaType), // meta
        TypeNameAssemblerImpl.INSTANCE.on(MapTypeApi::name), // name
        new MapType_ListAssembler<>(MapTypeApi::supertypes, INSTANCE), // supertypes
        DataTypeAssemblerImpl.INSTANCE.on(MapTypeApi::valueType) // value type
    );
  }
}
