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
import ws.epigraph.names.DataTypeName;
import ws.epigraph.services._resources.epigraph.projections.output.datatypename.DataTypeNameAsm;
import ws.epigraph.services._resources.epigraph.projections.output.datatypename.retrotagname.TagNameAsm;

import java.util.Optional;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class DataTypeNameAsmImpl extends DataTypeNameAsm<DataTypeName> {
  public static final DataTypeNameAsmImpl INSTANCE = new DataTypeNameAsmImpl();

  private DataTypeNameAsmImpl() {
    super(
        new TagNameAsm<>(
            (dtn, p, c) ->
                Optional.ofNullable(dtn.defaultTagName)
                    .<NameString.Value>map(retro -> NameString.create(retro).asValue())
                    .orElse(NameString.type.createValue(null))
        ),
        TypeNameAsmImpl.INSTANCE.from(dtn -> dtn.typeName)
    );
  }
}
