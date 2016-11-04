/* Created by yegor on 7/8/16. */

package ws.epigraph.scala

import java.nio.file.Path

import ws.epigraph.schema.compiler.CNamespace
import org.jetbrains.annotations.Nullable

import NewlineStringInterpolator.NewlineHelper

import scala.language.implicitConversions

class NamespaceGen(from: CNamespace) extends ScalaGen[CNamespace](from) {

  // TODO respect annotations changing namespace names for scala

  protected override def relativeFilePath: Path =
    ScalaGenUtils.fqnToPath(from.fqn).resolve("package.scala")

  protected def generate: String = sn"""\
/*
 * Standard header
 */
${if (from.parent ne null) s"\npackage ${scalaFqn(from.fqn.removeLastSegment())}\n" else ""}
import ws.epigraph.names

/**
 * Package object for `${from.fqn}` namespace.
 * TODO: doc annotation here
 */
package object ${scalaName(from.local)} {

  val namespace: names.QualifiedNamespaceName = new names.QualifiedNamespaceName(
    ${nsOpt(from.parent)}, names.LocalNamespaceName("${from.local}")
  )

}
"""

  private def nsOpt(@Nullable ns: String): String = if (ns == null) "None" else s"Some($ns.namespace)"

}
