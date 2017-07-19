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
import ws.epigraph.names.QualifiedName;
import ws.epigraph.services._resources.epigraph.projections.output.typenameprojection._nt.qualifiedtypename.name.QualifiedNameAssembler;

import java.util.Arrays;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class QualifiedNameAssemblerImpl extends QualifiedNameAssembler<QualifiedName, String> {
  public static final QualifiedNameAssemblerImpl INSTANCE = new QualifiedNameAssemblerImpl();

  private QualifiedNameAssemblerImpl() {
    super(
        qn -> Arrays.asList(qn.toFqn().segments),
        (d, p, c) -> NameString.create(d).asValue()
    );
  }
}
