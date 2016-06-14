/* Created by yegor on 6/10/16. */

package com.sumologic.epigraph.schema.compiler

import com.sumologic.epigraph.schema.parser.psi.{SchemaEnumTypeDef, SchemaListTypeDef, SchemaMapTypeDef, SchemaPrimitiveTypeDef, SchemaRecordTypeDef, _}
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._
import scala.collection.mutable

class CTypeRef(val csf: CSchemaFile, val psi: SchemaTypeRef)(implicit val ctx: CContext) {

  val name: CTypeName = psi match {
    case sftr: SchemaFqnTypeRef => csf.resolveLocalTypeRef(sftr)
    case sal: SchemaAnonList => new CListTypeName(csf, sal)
    case sam: SchemaAnonMap => new CMapTypeName(csf, sam)
    case _ => throw new RuntimeException // TODO exception
  }

  private var typeOpt: Option[CType] = None

  def getTypeOpt: Option[CType] = typeOpt

  def resolveTo(ctype: CType): this.type = {
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


class CTypeName protected(val name: String)(implicit val ctx: CContext) {

  def canEqual(other: Any): Boolean = other.isInstanceOf[CTypeName]

  override def equals(other: Any): Boolean = other match {
    case that: CTypeName => that.canEqual(this) && name == that.name
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(name)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

}


class CTypeFqn(fqn: String)(implicit ctx: CContext) extends CTypeName(fqn) {

  def this(ns: String, ln: String)(implicit ctx: CContext) = this(ns + "." + ln)

}

class CListTypeName(csf: CSchemaFile, psi: SchemaAnonList)(implicit ctx: CContext) extends {

  val elementTypeRef: CTypeRef = new CTypeRef(csf, psi.getTypeRef)

} with CTypeName("list[" + elementTypeRef.name.name + "]") {

}

class CMapTypeName(csf: CSchemaFile, val psi: SchemaAnonMap)(implicit ctx: CContext) extends {


  val keyTypeRef: CTypeRef = new CTypeRef(csf, psi.getTypeRefList.head)

  val valueTypeRef: CTypeRef = new CTypeRef(csf, psi.getTypeRefList.last)

} with CTypeName(
  "map[" + keyTypeRef.name.name + "," + valueTypeRef.name.name + "]"
) {

//  if (keyTypeRef.name.name == "epigraph.String") {
//    val lnu = new LineNumberUtil(csf.psi.getText, 4)
//    val off = psi.getTextRange.getStartOffset
//    println("Error: " + lnu.line(off) + ":" + lnu.column(off))
//  } else println("KOOL")

}


class CType(val csf: CSchemaFile, val psi: SchemaTypeDef)
    (implicit val ctx: CContext) { // TODO hierarchy/constructors for anon list/map

  val name: CTypeFqn = new CTypeFqn(csf.namespace.fqn, psi.getId.getText)

  val declaredSupertypeRefs: Seq[CTypeRef] = {
    @Nullable val sed: SchemaExtendsDecl = psi.getExtendsDecl
    if (sed == null) Nil else sed.getTypeRefList.map(new CTypeRef(csf, _))
  }

  val declaredSupplementees: Seq[CTypeRef] = {
    @Nullable val ssd: SchemaSupplementsDecl = psi.getSupplementsDecl
    if (ssd == null) Nil else ssd.getFqnTypeRefList.map(new CTypeRef(csf, _))
  }

  val injectedSupertypeRefs: mutable.Set[CTypeRef] = mutable.Set()

}

object CType {

  def apply(csf: CSchemaFile, stdw: SchemaTypeDefWrapper)(implicit ctx: CContext): CType = {
    val std: SchemaTypeDef = stdw.getElement
    val ctype = std match {
      case vt: SchemaVarTypeDef => new CVarType(csf, vt)
      case rt: SchemaRecordTypeDef => new CRecordType(csf, rt)
      case mt: SchemaMapTypeDef => new CMapType(csf, mt)
      case lt: SchemaListTypeDef => new CListType(csf, lt)
      case et: SchemaEnumTypeDef => new CEnumType(csf, et)
      case pt: SchemaPrimitiveTypeDef => new CPrimitiveType(csf, pt)
      case _ => null
    }
    ctype
  }

}

class CVarType(csf: CSchemaFile, override val psi: SchemaVarTypeDef)(implicit ctx: CContext) extends CType(csf, psi) {

  val declaredTags: Seq[CTag] = {
    @Nullable val body = psi.getVarTypeBody
    if (body == null) Nil else body.getVarTagDeclList.map(new CTag(csf, _)).toList
  }

}

class CTag(val csf: CSchemaFile, val psi: SchemaVarTagDecl)(implicit val ctx: CContext) {

  val name: String = psi.getName

  val typeRef: CTypeRef = new CTypeRef(csf, psi.getTypeRef)

}


class CRecordType(csf: CSchemaFile, override val psi: SchemaRecordTypeDef)(implicit ctx: CContext) extends CType(
  csf, psi
) {

  val declaredFields: Seq[CField] = {
    @Nullable val body = psi.getRecordTypeBody
    if (body == null) Nil else body.getFieldDeclList.map(new CField(csf, _)).toList
  }

}

class CField(val csf: CSchemaFile, val psi: SchemaFieldDecl)(implicit val ctx: CContext) {

  val name: String = psi.getName

  val typeRef: CTypeRef = new CTypeRef(csf, psi.getTypeRef)

}


class CMapType(csf: CSchemaFile, override val psi: SchemaMapTypeDef)(implicit ctx: CContext) extends CType(csf, psi) {

  val keyTypeRef: CTypeRef = new CTypeRef(csf, psi.getAnonMap.getTypeRefList.head)

  val valueTypeRef: CTypeRef = new CTypeRef(csf, psi.getAnonMap.getTypeRefList.last)

}


class CListType(csf: CSchemaFile, override val psi: SchemaListTypeDef)(implicit ctx: CContext) extends {

} with CType(csf, psi) {

  val elementTypeRef: CTypeRef = new CTypeRef(csf, psi.getAnonList.getTypeRef)

}


class CEnumType(csf: CSchemaFile, psi: SchemaEnumTypeDef)(implicit ctx: CContext) extends CType(csf, psi) {

  val values: Seq[CEnumValue] = {
    @Nullable val body = psi.getEnumTypeBody
    if (body == null) Nil else body.getEnumMemberDeclList.map(new CEnumValue(csf, _)).toList
  }

}

class CEnumValue(csf: CSchemaFile, psi: SchemaEnumMemberDecl)(implicit val ctx: CContext) {

  val name: String = psi.getName

}


class CPrimitiveType(csf: CSchemaFile, override val psi: SchemaPrimitiveTypeDef)(implicit ctx: CContext) extends CType(
  csf, psi
) {

  val kind: CPrimitiveKind = CPrimitiveKind.forKeyword( // TODO deal with all nulls?
    Seq(psi.getStringT, psi.getIntegerT, psi.getLongT, psi.getDoubleT, psi.getBooleanT).find(_ != null).get.getText
  )

}

class CSupplement(csf: CSchemaFile, val psi: SchemaSupplementDef)(implicit val ctx: CContext) {

  val source: CTypeRef = new CTypeRef(csf, psi.sourceRef())

  val targets: Seq[CTypeRef] = psi.supplementedRefs().map(new CTypeRef(csf, _))

}