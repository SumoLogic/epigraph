/* Created by yegor on 7/12/16. */

package ws.epigraph.scala

import java.nio.file.Path

import ws.epigraph.schema.compiler.CTypeDef

abstract class TypeScalaGen[TypeDef >: Null <: CTypeDef](from: TypeDef) extends ScalaGen[TypeDef](from) {

  protected val t: TypeDef = from

  protected override def relativeFilePath: Path = { // TODO respect annotations changing namespace/type names for scala
    ScalaGenUtils.fqnToPath(from.name.fqn.removeLastSegment()).resolve(from.name.local + ".scala")
  }

}
