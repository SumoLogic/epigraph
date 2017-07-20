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

package ws.epigraph.java.service.assemblers

import ws.epigraph.java.{ImportManager, JavaGen}
import ws.epigraph.lang.Qn

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait AsmGen extends JavaGen {
  protected def namespace: Qn
  protected def shortClassName: String

  protected lazy val importManager: ImportManager = new ImportManager(namespace)

  protected object Imports {
    val notNull: ImportManager.Imported =
      if (ctx.java8Annotations) importManager.use("org.jetbrains.annotations.NotNull").prepend("@") else ImportManager.empty
    val nullable: ImportManager.Imported =
      if (ctx.java8Annotations) importManager.use("org.jetbrains.annotations.Nullable").prepend("@") else ImportManager.empty
    val func: ImportManager.Imported = importManager.use("java.util.function.Function")
    val assembler: ImportManager.Imported = importManager.use("ws.epigraph.assembly.Asm")
    val assemblerContext: ImportManager.Imported = importManager.use("ws.epigraph.assembly.AsmContext")
    val _type: ImportManager.Imported = importManager.use("ws.epigraph.types.Type")
    val errValue: ImportManager.Imported = importManager.use("ws.epigraph.errors.ErrorValue")
  }

  protected def closeImports(): Unit = {
    val _ = Imports.assembler // cause lazy eval
    importManager.close()
  }
}
