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
import ws.epigraph.schema.Namespaces
import ws.epigraph.schema.operations.{OperationDeclaration, OperationKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
object ServiceNames {
  val operationKinds: Map[OperationKind, String] = Map(
    OperationKind.CREATE -> "create",
    OperationKind.READ -> "read",
    OperationKind.UPDATE -> "update",
    OperationKind.DELETE -> "delete",
    OperationKind.CUSTOM -> "custom"
  )

  def resourceNamespace(baseNamespace: Qn, resourceFieldName: String): Qn =
    new Namespaces(baseNamespace).resourceNamespace(resourceFieldName)

  def operationNamespace(baseNamespace: Qn, resourceFieldName: String, op: OperationDeclaration): Qn =
    new Namespaces(baseNamespace).operationNamespace(resourceFieldName, op.kind(), op.nameOrDefaultName())

  def clientNamespace(baseNamespace: Qn, resourceFieldName: String): Qn =
    new Namespaces(baseNamespace).clientNamespace(resourceFieldName)

  def transformerNamespace(baseNamespace: Qn, transformerName: String): Qn =
    new Namespaces(baseNamespace).transformerNamespace(transformerName)

}
