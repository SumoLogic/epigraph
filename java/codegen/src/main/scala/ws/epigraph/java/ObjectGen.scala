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

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ObjectGen[T](val obj: T) extends AbstractObjectGen {

  /** Generates an expression yielding an object of some type */
  override def generate(ctx: ObjectGenContext): String = {
    if (obj == null) "null"
    else {
      ctx.addImport(obj.getClass.getCanonicalName)
      generateObject(ctx)
    }
  }

  protected def generateObject(ctx: ObjectGenContext): String
}
