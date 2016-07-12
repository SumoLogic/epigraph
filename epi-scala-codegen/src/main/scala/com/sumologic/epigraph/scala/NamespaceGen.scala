/* Created by yegor on 7/8/16. */

package com.sumologic.epigraph.scala

import com.sumologic.epigraph.schema.compiler.CNamespace
import org.jetbrains.annotations.Nullable

import scala.language.implicitConversions

object NamespaceGen extends ScalaGen {

  final override type From = CNamespace

  def generate(ns: CNamespace): String =
    s"""
/*
 * Standard header
 */
${if (ns.parent ne null) s"package ${scalaFqn(ns.fqn.removeLastSegment())}\n" else ""}
import com.sumologic.epigraph.names

/**
 * Package object for `${ns.fqn}` namespace.
 * TODO: doc annotation here
 */
package object ${scalaName(ns.local)} {

  val namespace: names.QualifiedNamespaceName = new names.QualifiedNamespaceName(
    ${nsOpt(ns.parent)}, names.LocalNamespaceName("${ns.local}")
  )
  val namespace: names.QualifiedNamespaceName = new names.QualifiedNamespaceName(
    ${
      ?(
        ns.parent,
        s"Some(${scalaFqn(ns.fqn.removeLastSegment())}.namespace)",
        "None"
      )
    }, names.LocalNamespaceName("${ns.local}")
  )

}
""".trim

  def nsOpt(@Nullable ns: String): String = if (ns == null) "None" else s"Some($ns.namespace)"

}
