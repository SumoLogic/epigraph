/* Created by yegor on 6/9/16. */

package com.sumologic.epigraph.schema.compiler

import com.sumologic.epigraph.schema.parser.psi.SchemaTypeDef

import scala.collection.mutable


class CTypeRef(val name: CTypeName) {

  private var typeOpt: Option[CType] = None

  def getTypeOpt: Option[CType] = typeOpt

  def resolve(ctype: CType): this.type = {
    typeOpt match {
      case Some(ct) if !(ct eq ctype) => throw new RuntimeException // TODO proper exception
      case None =>
        if (ctype.name == name) {
          typeOpt = Some(ctype)
        } else {
          throw new RuntimeException // TODO proper exception
        }
    }
    this
  }

}

class CTypeName protected(val name: String) {

  def canEqual(other: Any): Boolean = other.isInstanceOf[CTypeName]

  override def equals(other: Any): Boolean = other match {
    case that: CTypeName => (that canEqual this) && name == that.name
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(name)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString: String = "«" + name + "»"
}

class CTypeFqn private(fqn: String) extends CTypeName(fqn) {

//  private def this(ns: String, std: SchemaTypeDef) = this(ns + "." + std.getName)

}

object CTypeFqn {

  private val cache = mutable.Map[String, CTypeFqn]()

  def apply(fqn: String): CTypeFqn = cache.getOrElseUpdate(fqn, new CTypeFqn(fqn))

  def apply(ns: String, std: SchemaTypeDef): CTypeFqn = apply(ns + "." + std.getName)

}

class CListTypeName(val elementTypeRef: CTypeRef) extends CTypeName("list[" + elementTypeRef.name.name + "]")

class CMapTypeName(val keyTypeRef: CTypeRef, val valueTypeRef: CTypeRef) extends CTypeName(
  "map[" + keyTypeRef.name.name + "," + valueTypeRef.name.name + "]"
)

class CType(val name: CTypeName) {

  val declaredSupertypeRefs: mutable.Set[CTypeRef] = mutable.Set()

  val injectedSupertypeRefs: mutable.Set[CTypeRef] = mutable.Set()

}


class CVarType(name: CTypeFqn) extends CType(name) {

  def this(fqn: String) = this(CTypeFqn(fqn))

  val declaredTags: mutable.Map[String, CTag] = mutable.Map()

}

class CTag(val name: String, val typeRef: CTypeRef) {

}


class CRecordType(name: CTypeFqn) extends CType(name) {

  val declaredFields: mutable.Map[String, CField] = mutable.Map()

}

class CField(name: String, val typeRef: CTypeRef) {

}


class CMapType(name: CTypeName, val keyTypeRef: CTypeRef, val valueTypeRef: CTypeRef) extends CType(name) {

}


class CListType(name: CTypeName, val elementTypeRef: CTypeRef) extends CType(name) {

}


class CEnumType(name: CTypeFqn) extends CType(name) {

  val values: mutable.Map[String, CEnumValue] = mutable.Map()

}

class CEnumValue(name: String) {

}


class CPrimitiveType(name: CTypeFqn, val kind: CPrimitiveKind) extends CType(name) {

}

