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
import ws.epigraph.services._resources.epigraph.operations.read.types.output.NameString_Type_MapAssembler;
import ws.epigraph.types.TypeApi;

import java.util.Map;
import java.util.function.Function;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class NameString_Type_MapAssemblerImpl extends NameString_Type_MapAssembler<
    Map<String, ? extends TypeApi>,
    String,
    TypeApi> {

  public static final NameString_Type_MapAssemblerImpl INSTANCE = new NameString_Type_MapAssemblerImpl();

  private NameString_Type_MapAssemblerImpl() {
    super(
        NameString::create,
        Function.identity(),
        TypeAssemblerImpl.INSTANCE
    );
  }
}
