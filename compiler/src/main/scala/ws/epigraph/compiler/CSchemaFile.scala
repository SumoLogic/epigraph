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

/* Created by yegor on 6/9/16. */

package ws.epigraph.compiler

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.BiFunction
import java.util.regex.Pattern

import com.intellij.psi.PsiElement
import ws.epigraph.lang.Qn
import ws.epigraph.schema.parser.psi._
import net.jcip.annotations.ThreadSafe
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._

class CSchemaFile(val psi: SchemaFile)(implicit val ctx: CContext) {

  val filename: String = psi.getName

  val lnu: LineNumberUtil = new LineNumberUtil(psi.getText, ctx.tabWidth) // TODO also get tab width from file itself?

  @ThreadSafe
  val typerefs: ConcurrentLinkedQueue[CTypeRef] = new java.util.concurrent.ConcurrentLinkedQueue

  @ThreadSafe
  val dataTypes: ConcurrentLinkedQueue[CDataType] = new java.util.concurrent.ConcurrentLinkedQueue

  val namespace: CNamespace = new CNamespace(this, psi.getNamespaceDecl)

  val imports: Map[String, CImport] = psi.getImportStatements.map(new CImport(_)).map { ci =>
    (ci.alias, ci)
  }(collection.breakOut) // TODO deal with dupes (foo.Baz and bar.Baz)

  val importedAliases: Map[String, Qn] = ctx.implicitImports ++ imports.map { case (alias, ci) => (alias, ci.fqn) }

  @Nullable private val defs: SchemaDefs = psi.getDefs

  val typeDefs: Seq[CTypeDef] = if (defs == null) Nil else defs.getTypeDefWrapperList.map(CTypeDef.apply(this, _))

  val supplements: Seq[CSupplement] = if (defs == null) Nil else defs.getSupplementDefList.map(new CSupplement(this, _))

  ctx.schemaFiles.put(filename, this)

  def qualifyLocalTypeRef(sftr: SchemaQnTypeRef): CTypeFqn = {
    val alias = sftr.getQn.getQn.first
    val parentNamespace = importedAliases.get(alias) match {
      case Some(fqn) => fqn.removeLastSegment() // typeref starting with imported alias
      case None => sftr.getQn.getQn.size match {
        case 1 => namespace.fqn // single-segment typeref to a type in schema document namespace
        case _ => Qn.EMPTY // fully qualified typeref
      }
    }
    new CTypeFqn(this, parentNamespace, sftr)
  }

  def position(psi: PsiElement): CErrorPosition = {
    // TODO check element is ours?
    lnu.pos(psi)
  }

  def location(psi: PsiElement): String = {
    val cep = position(psi)
    filename + ":" + cep.line + ":" + cep.column
  }

}

class CNamespace(val csf: CSchemaFile, val psi: SchemaNamespaceDecl)(implicit val ctx: CContext) {

  val fqn: Qn = psi.getFqn

  val local: String = validate(fqn.last())

  @Nullable val parent: String = if (fqn.size == 1) null else fqn.removeLastSegment().toString

  // TODO expose custom attributes

  ctx.namespaces.merge(fqn.toString, this, CNamespace.MergeFunction)

  private def validate(local: String)(implicit ctx: CContext): String = {
    if (!CNamespace.LocalNamespaceNamePattern.matcher(local).matches) ctx.errors.add(
      CError(csf.filename, csf.position(psi), s"Invalid namespace name '$local'")
    )
    local
  }

}

object CNamespace {

  val LocalNamespaceNamePattern: Pattern = """\p{Lower}\p{Alnum}*""".r.pattern

  val MergeFunction: BiFunction[CNamespace, CNamespace, CNamespace] =
    new BiFunction[CNamespace, CNamespace, CNamespace] {
      // TODO merge custom attrs etc. properly
      override def apply(oldNs: CNamespace, newNs: CNamespace): CNamespace = newNs
    }

}

class CImport(@Nullable val psi: SchemaImportStatement)(implicit val ctx: CContext) {

  val fqn: Qn = psi.getQn.getQn

  val alias: String = fqn.last

}
