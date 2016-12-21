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

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class NativePrimitiveGen(obj: Any) extends AbstractServiceGen {
  override def generate(ctx: ServiceGenContext): String = obj match {
    case s: java.lang.String => s"""$s"""
    case i: java.lang.Integer => s"Integer.valueOf($i)"
    case l: java.lang.Long => s"Long.valueOf($l)"
    case f: java.lang.Float => s"Float.valueOf(d)"
    case d: java.lang.Double => s"Double.valueOf(d)"
    case _ => throw new IllegalArgumentException("Unsupported native primitive kind: " + obj.getClass.getName)
  }
}
