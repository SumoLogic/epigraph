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

/* Created by yegor on 4/29/16. */

package ws.epigraph.gen

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
