/* Created by yegor on 6/17/16. */

package com.sumologic.epigraph.schema.compiler


object JavaFunction {

  def apply[T, R](function1: T => R): java.util.function.Function[T, R] = new JavaFunction[T, R](function1)

}

private class JavaFunction[T, R](sf: T => R) extends java.util.function.Function[T, R] {

  override def apply(t: T): R = sf.apply(t)

}
