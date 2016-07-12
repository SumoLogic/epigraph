/* Created by yegor on 7/11/16. */

package com.sumologic.epigraph.scala

trait ScalaGen {

  type From >: Null <: AnyRef

  def generate(from: From): String

}
