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

/* Created by yegor on 7/1/16. */

package ws.epigraph.edl.compiler

import ws.epigraph.edl.parser.psi.{EdlAnonList, EdlAnonMap, EdlQnTypeRef, EdlTypeRef}


object CTypeRef {

  def apply(csf: CEdlFile, psi: EdlTypeRef)(implicit ctx: CContext): CTypeRef = psi match {
    case psi: EdlQnTypeRef => apply(csf, psi)
    case psi: EdlAnonList => apply(csf, psi)
    case psi: EdlAnonMap => apply(csf, psi)
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  def apply(csf: CEdlFile, psi: EdlQnTypeRef)(implicit ctx: CContext): CTypeDefRef =
    new CTypeDefRef(csf, psi)

  def apply(csf: CEdlFile, psi: EdlAnonList)(implicit ctx: CContext): CAnonListTypeRef =
    new CAnonListTypeRef(csf, psi)

  def apply(csf: CEdlFile, psi: EdlAnonMap)(implicit ctx: CContext): CAnonMapTypeRef =
    new CAnonMapTypeRef(csf, psi)

  def apply(ctype: CType)(implicit ctx: CContext): CTypeRef = ctype match {
    case ctype: CTypeDef => new CTypeDefRef(ctype.csf, ctype.name).resolveTo(ctype)
    case ctype: CAnonListType => new CAnonListTypeRef(ctype.elementDataType).resolveTo(ctype)
    case ctype: CAnonMapType => new CAnonMapTypeRef(ctype.keyTypeRef, ctype.valueDataType).resolveTo(ctype)
  }

}

abstract class CTypeRef protected(val csf: CEdlFile)(implicit val ctx: CContext) {

  type Name <: CTypeName

  type Type <: CType

  val name: Name

  @throws[java.util.NoSuchElementException]("If typeref hasn't been resolved")
  def resolved: Type = typeOptVar.get

  def isResolved: Boolean = typeOptVar.nonEmpty

  private var typeOptVar: Option[Type] = None

  csf.typerefs.add(this)

  def resolveTo(ctype: Type): this.type = {
    typeOptVar.foreach(resolved => assert(resolved eq ctype, typeOptVar.get.name.name))
    assert(ctype.name == name)
    typeOptVar = Some(ctype)
    this
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[CTypeRef]

  override def equals(other: Any): Boolean = other match {
    case that: CTypeRef => (that canEqual this) && name == that.name
    case _ => false
  }

  override def hashCode(): Int = name.hashCode

}


class CTypeDefRef(csf: CEdlFile, override val name: CTypeFqn)(implicit ctx: CContext) extends CTypeRef(csf) {

  def this(csf: CEdlFile, psi: EdlQnTypeRef)(implicit ctx: CContext) = this(csf, csf.qualifyLocalTypeRef(psi))

  final override type Name = CTypeFqn

  final override type Type = CTypeDef

}


class CAnonListTypeRef(val elementDataType: CDataType)(implicit ctx: CContext) extends CTypeRef(elementDataType.csf) {

  def this(csf: CEdlFile, psi: EdlAnonList)(implicit ctx: CContext) =
    this(new CDataType(csf, psi.getValueTypeRef))

  final override type Name = CAnonListTypeName

  final override type Type = CAnonListType

  final override val name: CAnonListTypeName = new CAnonListTypeName(elementDataType)

}


class CAnonMapTypeRef(val keyTypeRef: CTypeRef, val valueDataType: CDataType)(implicit ctx: CContext)
    extends CTypeRef(valueDataType.csf) {

  def this(csf: CEdlFile, psi: EdlAnonMap)(implicit ctx: CContext) =
    this(CTypeRef(csf, psi.getTypeRef), new CDataType(csf, psi.getValueTypeRef))

  final override type Name = CAnonMapTypeName

  final override type Type = CAnonMapType

  override val name: CAnonMapTypeName = new CAnonMapTypeName(keyTypeRef, valueDataType)

}
