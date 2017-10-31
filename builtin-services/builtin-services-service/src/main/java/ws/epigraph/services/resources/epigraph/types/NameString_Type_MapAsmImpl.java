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

import epigraph.schema.NameString;
import ws.epigraph.services._resources.epigraph.operations.read.types.output.NameString_Type_MapAsm;
import ws.epigraph.types.TypeApi;
import ws.epigraph.util.Function2;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class NameString_Type_MapAsmImpl
    extends NameString_Type_MapAsm<Map<String, ? extends TypeApi>, String, TypeApi> {

  public static final NameString_Type_MapAsmImpl INSTANCE = new NameString_Type_MapAsmImpl();

  private NameString_Type_MapAsmImpl() {
    super(
        Function2.identity1(),
        NameString::create,
        TypeAsmImpl.INSTANCE
    );
  }
}
