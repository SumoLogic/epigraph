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

import ws.epigraph.annotations.{Annotation, Annotations}
import ws.epigraph.gdata.{GData, GDatum}
import ws.epigraph.java.gdata.{GDataGen, GDatumGen}
import ws.epigraph.lang.{Qn, TextLocation}
import ws.epigraph.refs.{TypeRef, ValueTypeRef}
import ws.epigraph.types.TypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ObjectGenerators {
  def genOpt(obj: Any, ctx: ObjectGenContext): String

  def gen(obj: Any, ctx: ObjectGenContext): String =
    Option(genOpt(obj, ctx)).getOrElse {
      try {
        new NativePrimitiveGen(obj).generate(ctx)
      } catch {
        case _: IllegalArgumentException =>
          throw new IllegalArgumentException(s"Unsupported object kind '${ obj.getClass.getName }' ( $obj )")
      }
    }
}

object ObjectGenerators extends ObjectGenerators {
  def genOpt(obj: Any, ctx: ObjectGenContext): String =
    if (obj == null) "null"
    else obj match {

      case s: String => s""""${ escapeString(s) }""""
      case i: java.lang.Integer => i.toString
      case l: java.lang.Long => l.toString + "L"
      case d: java.lang.Double => d.toString + "d"
      case f: java.lang.Float => f.toString + "f"
      case b: java.lang.Boolean => b.toString

      case t: TypeApi => ObjectGenUtils.genTypeExpr(t, ctx.gctx)

      case qn: Qn => new QnGen(qn).generate(ctx)

      case tr: TypeRef => new TypeRefGen(tr).generate(ctx)
      case vtr: ValueTypeRef => new ValueTypeRefGen(vtr).generate(ctx)
      case tl: TextLocation => new TextLocationGen(tl).generate(ctx)

      case gdata: GData => new GDataGen(gdata).generate(ctx)
      case gdatum: GDatum => new GDatumGen(gdatum).generate(ctx)

      case ann: Annotation => new AnnotationGen(ann).generate(ctx)
      case anns: Annotations => new AnnotationsGen(anns).generate(ctx)

      case _ => null
    }

  def escapeString(s: String): String = // todo proper string escaping
    s
      .replace("\\", "\\\\")
      .replace("\t", "\\t")
      .replace("\n", "\\n")
      .replace("\"", "\\\"")
}