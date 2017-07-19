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
import ws.epigraph.assembly.Assembler;
import ws.epigraph.names.AnonListTypeName;
import ws.epigraph.names.AnonMapTypeName;
import ws.epigraph.names.QualifiedTypeName;
import ws.epigraph.names.TypeName;
import ws.epigraph.services._resources.epigraph.projections.output.typenameprojection.TypeNameAssembler;
import ws.epigraph.services._resources.epigraph.projections.output.typenameprojection.string.OutputNameStringProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class TypeNameAssemblerImpl extends TypeNameAssembler<TypeName> {
  public static final TypeNameAssemblerImpl INSTANCE = new TypeNameAssemblerImpl();

  public static final Assembler<TypeName, OutputNameStringProjection, NameString.Value> TYPE_NAME_ASSEMBLER =
      (d, p, c) -> NameString.create(d.toString()).asValue();

  private TypeNameAssemblerImpl() {
    super(
        tn -> {
          if (tn instanceof QualifiedTypeName) return epigraph.schema.QualifiedTypeName.type;
          else if (tn instanceof AnonListTypeName) return epigraph.schema.AnonListTypeName.type;
          else if (tn instanceof AnonMapTypeName) return epigraph.schema.AnonMapTypeName.type;
          else throw new IllegalArgumentException(tn.getClass().getName());
        },
        TYPE_NAME_ASSEMBLER,
        QualifiedTypeNameAssemblerImpl.INSTANCE.on(tn -> (QualifiedTypeName) tn),
        AnonListTypeNameAssemblerImpl.INSTANCE.on(tn -> (AnonListTypeName) tn),
        AnonMapTypeNameAssemblerImpl.INSTANCE.on(tn -> (AnonMapTypeName) tn)
    );
  }

}
