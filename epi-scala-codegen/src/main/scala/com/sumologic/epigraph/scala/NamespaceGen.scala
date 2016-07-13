/* Created by yegor on 7/8/16. */

package com.sumologic.epigraph.scala

import java.nio.file.Path

import com.sumologic.epigraph.schema.compiler.CNamespace
import org.jetbrains.annotations.Nullable

import scala.language.implicitConversions

class NamespaceGen(from: CNamespace) extends ScalaGen[CNamespace](from) {

  // TODO respect annotations changing namespace names for scala

  protected override def relativeFilePath: Path =
    GenUtils.fqnToPath(from.fqn).resolve("package.scala")

  protected def generate: String = s"""
/*
 * Standard header
 */
${if (from.parent ne null) s"package ${scalaFqn(from.fqn.removeLastSegment())}\n" else ""}
import com.sumologic.epigraph.names

/**
 * Package object for `${from.fqn}` namespace.
 * TODO: doc annotation here
 */
package object ${scalaName(from.local)} {

  val namespace: names.QualifiedNamespaceName = new names.QualifiedNamespaceName(
    ${nsOpt(from.parent)}, names.LocalNamespaceName("${from.local}")
  )

}
""".trim

  private def nsOpt(@Nullable ns: String): String = if (ns == null) "None" else s"Some($ns.namespace)"

}
