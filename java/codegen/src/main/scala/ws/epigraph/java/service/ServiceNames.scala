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

package ws.epigraph.java.service

import ws.epigraph.lang.Qn
import ws.epigraph.schema.operations.OperationDeclaration

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
object ServiceNames {
  def resourceNamespace(baseNamespace: Qn, resourceFieldName: String): Qn =
    baseNamespace.append("resources").append(resourceFieldName.toLowerCase)

  def operationNamespace(baseNamespace: Qn, resourceFieldName: String, op: OperationDeclaration): Qn =
    resourceNamespace(baseNamespace, resourceFieldName)
      .append("operations")
      .append(s"${op.kind()}${Option(op.name()).getOrElse("")}".toLowerCase)

}
