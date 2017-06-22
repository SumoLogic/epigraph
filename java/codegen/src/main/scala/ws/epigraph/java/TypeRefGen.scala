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

import ws.epigraph.java.ObjectGenerators.gen
import ws.epigraph.refs.{AnonListRef, AnonMapRef, QnTypeRef, TypeRef}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class TypeRefGen(ref: TypeRef) extends ObjectGen[TypeRef](ref) {

  override protected def generateObject(ctx: ObjectGenContext): String = ref match {
    case qr: QnTypeRef =>
      s"new QnTypeRef(${gen(qr.qn(), ctx)})"

    case lr: AnonListRef =>
      s"new AnonListRef(${gen(lr.itemsType(), ctx)})"

    case mr: AnonMapRef =>
      s"new AnonMapRef(${gen(mr.keysType(), ctx)}, ${gen(mr.itemsType(), ctx)})"

    case _ => throw new IllegalArgumentException("Unknown ref type: " + ref.getClass.getName)
  }

}
