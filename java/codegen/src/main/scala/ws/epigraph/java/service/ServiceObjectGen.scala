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

package ws.epigraph.java.service

import ws.epigraph.gdata.{GData, GDatum}
import ws.epigraph.java.service.gdata.{GDataGen, GDatumGen}
import ws.epigraph.java.service.projections.{AnnotationGen, AnnotationsGen}
import ws.epigraph.lang.{Qn, TextLocation}
import ws.epigraph.projections.{Annotation, Annotations}
import ws.epigraph.refs.{TypeRef, ValueTypeRef}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ServiceObjectGen[T](val obj: T) extends AbstractServiceGen {

  override def generate(ctx: ServiceGenContext): String = {
    if (obj == null) "null"
    else {
      ctx.addImport(obj.getClass.getCanonicalName)
      generateObject(ctx)
    }
  }

  protected def generateObject(ctx: ServiceGenContext): String
}

object ServiceObjectGen {
  def gen(obj: Any, ctx: ServiceGenContext): String =
    if (obj == null) "null"
    else obj match {
      case qn: Qn => new QnGen(qn).generate(ctx)
      case tr: TypeRef => new TypeRefGen(tr).generate(ctx)
      case vtr: ValueTypeRef => new ValueTypeRefGen(vtr).generate(ctx)
      case tl: TextLocation => new TextLocationGen(tl).generate(ctx)
      case gdata: GData => new GDataGen(gdata).generate(ctx)
      case gdatum: GDatum => new GDatumGen(gdatum).generate(ctx)
      case ann: Annotation => new AnnotationGen(ann).generate(ctx)
      case anns: Annotations => new AnnotationsGen(anns).generate(ctx)
      case _ =>
        try {
          new NativePrimitiveGen(obj).generate(ctx)
        } catch {
          case iae: IllegalArgumentException =>
            throw new IllegalArgumentException("Unsupported object kind: " + obj.getClass.getName)
        }
    }
}
