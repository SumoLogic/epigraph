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

import ws.epigraph.services._resources.epigraph.projections.output.datumtype._nt.maptype.MapTypeAsm;
import ws.epigraph.services._resources.epigraph.projections.output.datumtype._nt.maptype.supertypes.MapType_ListAsm;
import ws.epigraph.types.MapTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class MapTypeAsmImpl extends MapTypeAsm<MapTypeApi> {
  public static final MapTypeAsmImpl INSTANCE = new MapTypeAsmImpl();

  private MapTypeAsmImpl() {
    super(
        TypeAsmImpl.ABSTRACT_ASM.from(t -> (MapTypeApi) t), // abstract
        AnnotationsAsmImpl.INSTANCE.from(MapTypeApi::annotations), // annotations
        DatumTypeAsmImpl.INSTANCE.from(MapTypeApi::keyType), // key type
        DatumTypeAsmImpl.INSTANCE.from(MapTypeApi::metaType), // meta
        TypeNameAsmImpl.INSTANCE.from(MapTypeApi::name), // name
        new MapType_ListAsm<>(MapTypeApi::supertypes, INSTANCE), // supertypes
        DataTypeAsmImpl.INSTANCE.from(MapTypeApi::valueType) // value type
    );
  }
}
