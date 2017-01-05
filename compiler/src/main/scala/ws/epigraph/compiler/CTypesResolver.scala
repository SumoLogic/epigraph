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

package ws.epigraph.compiler

import com.intellij.psi.PsiElement
import ws.epigraph.refs._
import ws.epigraph.types.Type

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class CTypesResolver(csf: CSchemaFile)(implicit ctx: CContext) extends TypesResolver {
  // todo: this is a temporary bridge, must be removed once op projections + idl + parsers are ported to scala/ctypes

  override def resolve(reference: QnTypeRef): Type = {
    val cTypeDef = ctx.typeDefs.get(new CTypeFqn(csf, reference.qn(), null.asInstanceOf[PsiElement]))
    ???
  }

  override def resolve(reference: AnonListRef): Type = {
    val valueTypeRef = reference.itemsType()
    val defaultOverride = valueTypeRef.defaultOverride()
    val cType = valueTypeRef.typeRef().resolve(this).asInstanceOf[CTypeApiWrapper].cType
    val cDataType: CDataType = new CDataType(csf, cType.selfRef, Option(defaultOverride))

    val cListType = ctx.anonListTypes.get(cDataType)

    ???
  }

  override def resolve(reference: AnonMapRef): Type = {
    val keyCType: CType = reference.keysType().resolve(this).asInstanceOf[CTypeApiWrapper].cType

    val valueTypeRef = reference.itemsType()
    val defaultOverride = valueTypeRef.defaultOverride()
    val valueCType = valueTypeRef.typeRef().resolve(this).asInstanceOf[CTypeApiWrapper].cType
    val valueCDataType: CDataType = new CDataType(csf, valueCType.selfRef, Option(defaultOverride))

    val cMapType = ctx.anonMapTypes.get((keyCType.selfRef, valueCDataType))

    ???
  }
}
