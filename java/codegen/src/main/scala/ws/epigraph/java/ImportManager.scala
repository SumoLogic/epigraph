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

package ws.epigraph.java

import ws.epigraph.lang.Qn

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ImportManager(val namespace: Qn, alwaysUseQualified: Boolean = false) {
  private var closed: Boolean = false
  private var fqnToImportedNames = Map[Qn, ImportedName]()

  def use(fqn: Qn): ImportedName = {
    assertOpen()
    fqnToImportedNames.getOrElse(
      fqn, {
        val in = new ImportedName(fqn)
        fqnToImportedNames += (fqn -> in)
        in
      }
    )
  }

  def use(name: String): ImportedName = use(toFqn(name))

  def toFqn(name: String): Qn = {
    val qn = Qn.fromDotSeparated(name)

//    val csi = qn.segments.indexWhere(_.charAt(0).isUpper)
//
//    val ns = if (csi < 0) qn else if (csi == qn.size()) Qn.EMPTY else qn.takeHeadSegments(csi)
//    val shortClassName = if (csi < 0) null else if (csi == 0) qn else qn.removeHeadSegments(csi)
//    val nameToImport = if (csi < 0 || csi == qn.size - 1) qn else qn.takeHeadSegments(csi + 1)

    qn
  }

  def close(): Unit = {
    if (!closed) {
      val importedNames = fqnToImportedNames.values.toList.sorted // sorted to make unit testing easier
      var visibleShortNames = Set[String]()

      // check same namespace imports
      importedNames.filter(_.inNamespace).foreach { in =>
        in.isImportedOpt = Some(true)
        visibleShortNames += in.shortName
      }

      // check java.lang imports
      importedNames.filter(_.explicitlyImported).foreach { in =>
        val isImported = !visibleShortNames.contains(in.shortName)
        if (isImported) visibleShortNames += in.shortName
        in.isImportedOpt = Some(isImported)
      }

      // the rest
      importedNames.filter(_.isImportedOpt.isEmpty).foreach { in =>
        val isImported = !visibleShortNames.contains(in.shortName)
        if (isImported) visibleShortNames += in.shortName
        in.isImportedOpt = Some(isImported)
      }

      closed = true
    }

  }

  lazy val imports: Set[String] = {
    assertClosed()
    fqnToImportedNames.values
      .filter(in => in.isImportedOpt.get && !in.explicitlyImported && !in.inNamespace)
      .map(_.fqn.toString)
      .toSet
  }

  private def assertOpen(): Unit = {
    if (closed) throw new IllegalStateException("ImportManager is already closed")
  }

  private def assertClosed(): Unit = {
    if (!closed) throw new IllegalStateException("ImportManager.close() not called yet")
  }


  class ImportedName(val fqn: Qn) extends ImportManager.Imported with Ordered[ImportedName] {
    def inNamespace: Boolean = fqn.removeLastSegment() == namespace

    def explicitlyImported: Boolean = fqn.removeLastSegment() == ImportManager.javaLang

    private[java] var isImportedOpt: Option[Boolean] = None

    def shortName: String = fqn.last()

    override def toString: String = {
      assertClosed()
      if (alwaysUseQualified) fqn.toString
      else isImportedOpt.map { isImported =>
        if (isImported) fqn.last()
        else fqn.toString
      }.getOrElse {
        throw new RuntimeException(s"can't happen: $namespace | $fqn")
      }
    }

    def canEqual(other: Any): Boolean = other.isInstanceOf[ImportedName]

    override def equals(other: Any): Boolean = other match {
      case that: ImportedName =>
        (that canEqual this) &&
        fqn == that.fqn
      case _ => false
    }

    override def hashCode(): Int = {
      val state = Seq(fqn)
      state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }

    override def compare(that: ImportedName): Int = fqn.compareTo(that.fqn)
  }

}

object ImportManager {
  private val javaLang = Qn.fromDotSeparated("java.lang")

  val empty: Imported = new Imported {override def toString: String = "" }

  trait Imported /*extends Ordered[Imported]*/ {
    override def toString: String

    def prepend(s: String): Imported = {
      lazy val ts = toString
      new Imported {
        override def toString: String = s + ts
      }
    }

//    override def compare(that: Imported): Int = toString.compareTo(that.toString)
  }

}
