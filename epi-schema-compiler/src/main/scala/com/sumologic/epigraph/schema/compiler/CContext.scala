/* Created by yegor on 6/9/16. */

package com.sumologic.epigraph.schema.compiler

import scala.collection.mutable

class CContext {

  //val types: mutable.Map[CTypeName, CType] = mutable.Map()

  val errors: mutable.Seq[CError] = mutable.Seq()

}

class CError