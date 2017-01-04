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

package ws.epigraph.compiler.projections

import ws.epigraph.compiler._
import ws.epigraph.schema.parser.psi.{SchemaAnonList, SchemaAnonMap, SchemaQnTypeRef, SchemaTypeRef}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
object CProjectionParsingUtil {
  def parseType(psi: SchemaTypeRef, csf: CSchemaFile)(implicit ctx: CContext): CType =
    Option(psi match {
      case qnRef: SchemaQnTypeRef =>
        val name: CTypeName = new CTypeFqn(csf, csf.namespace.fqn, qnRef)
        ctx.typeDefs.get(name)
      case anonListRef: SchemaAnonList =>
        val valueDataType = new CDataType(csf, anonListRef.getValueTypeRef)
        ctx.anonListTypes.get(valueDataType)
      case anonMapRef: SchemaAnonMap =>
        val k: CTypeRef = CTypeRef(csf, anonMapRef.getTypeRef)
        val v: CDataType = new CDataType(csf, anonMapRef.getValueTypeRef)
        ctx.anonMapTypes.get((k, v))
    }).getOrElse{
      ErrorReporter.reporter(csf).error(s"Can't resolve type '${psi.getText}'", psi)
      throw new CompilerException
    }
}
