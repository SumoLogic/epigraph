/*
 * Copyright 2016 Sumo Logic
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

/* Created by yegor on 7/12/16. */

package ws.epigraph.scala

import java.nio.file.Path

import ws.epigraph.compiler.CTypeDef

abstract class TypeScalaGen[TypeDef >: Null <: CTypeDef](from: TypeDef) extends ScalaGen[TypeDef](from) {

  protected val t: TypeDef = from

  protected override def relativeFilePath: Path = { // TODO respect annotations changing namespace/type names for scala
    ScalaGenUtils.fqnToPath(from.name.fqn.removeLastSegment()).resolve(from.name.local + ".scala")
  }

}
