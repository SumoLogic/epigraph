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

import ws.epigraph.refs.{AnonListRef, AnonMapRef, QnTypeRef, TypeRef}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class TypeRefGen(ref: TypeRef) extends ServiceObjectGen[TypeRef](ref) {

  override protected def generateObjectNoIndent(ctx: ServiceGenContext): String = ref match {
    case qr: QnTypeRef =>
      s"new QnTypeRef(${new QnGen(qr.qn()).generate(ctx)})"

    case lr: AnonListRef =>
      s"new AnonListRef(${new ValueTypeRefGen(lr.itemsType()).generate(ctx)})"

    case mr: AnonMapRef =>
      s"new AnonMapRef(${new TypeRefGen(mr.keysType()).generate(ctx)}, ${new ValueTypeRefGen(mr.itemsType()).generate(ctx)})"

    case _ => throw new IllegalArgumentException("Unknown ref type: " + ref.getClass.getName)
  }

}
