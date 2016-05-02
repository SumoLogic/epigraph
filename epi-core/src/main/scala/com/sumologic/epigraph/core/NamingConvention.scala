/* Created by yegor on 4/29/16. */

package com.sumologic.epigraph.core

import scala.util.matching.Regex

trait NamingConvention {

  def isValidName(string: String): Boolean

}


object NamingConvention {

  val LowerCamelCase: NamingConvention = new RegexNamingConvention("""\p{Lower}\p{Alnum}*""".r)

  val UpperCamelCase: NamingConvention = new RegexNamingConvention("""\p{Upper}\p{Alnum}*""".r)


  private class RegexNamingConvention(private val pattern: Regex) extends NamingConvention {

    override def isValidName(string: String): Boolean = { // TODO add reporting of violation position and nature?
      string match {
        case pattern() => true
        case _ => false
      }
    }

  }


}


