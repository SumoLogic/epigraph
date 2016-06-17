/* Created by yegor on 6/10/16. */

package com.sumologic.epigraph.schema.compiler

import java.util.concurrent.ConcurrentLinkedQueue

import com.intellij.psi.PsiElement
import com.sumologic.epigraph.schema.parser.Fqn
import com.sumologic.epigraph.schema.parser.psi._
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._
import scala.collection.mutable

class CTypeRef(val csf: CSchemaFile, val psi: SchemaTypeRef)(implicit val ctx: CContext) {

  val name: CTypeName = psi match { // TODO hierarchy for typedefrefs vs anontyperefs?
    case sftr: SchemaFqnTypeRef => csf.resolveLocalTypeRef(sftr)
    case sal: SchemaAnonList => new CAnonListTypeName(csf, sal)
    case sam: SchemaAnonMap => new CAnonMapTypeName(csf, sam)
    case _ => throw new RuntimeException // TODO exception
  }

  private var typeOptVar: Option[CType] = None

  csf.typerefs.add(this)

  def getTypeOpt: Option[CType] = typeOptVar

  @throws[java.util.NoSuchElementException]
  def resolved: CType = typeOptVar.get

  def resolveTo(ctype: CType): this.type = {
    typeOptVar match {
      case Some(ct) if !(ct eq ctype) => throw new RuntimeException // TODO proper exception
      case None =>
        if (ctype.name == name) {
          typeOptVar = Some(ctype)
        } else {
          throw new RuntimeException // TODO proper exception
        }
    }
    this
  }

}


abstract class CTypeName protected(val csf: CSchemaFile, val name: String, val psi: PsiElement)
    (implicit val ctx: CContext) {

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


class CTypeFqn private(csf: CSchemaFile, fqn: Fqn, psi: PsiElement)(implicit ctx: CContext) extends CTypeName(
  csf, fqn.toString, psi
) {

  def this(csf: CSchemaFile, parentNs: Fqn, lqn: SchemaFqnTypeRef)(implicit ctx: CContext) = this(
    csf, parentNs.append(lqn.getFqn.getFqn), lqn: PsiElement
  )

  def this(csf: CSchemaFile, parentNs: Fqn, typeDef: SchemaTypeDef)(implicit ctx: CContext) = this(
    csf, parentNs.append(typeDef.getQid.getCanonicalName), typeDef.getQid.getId: PsiElement
  )

}

class CAnonListTypeName(csf: CSchemaFile, override val psi: SchemaAnonList)(implicit ctx: CContext) extends {

  val elementTypeRef: CTypeRef = new CTypeRef(csf, psi.getTypeRef)

} with CTypeName(csf, "list[" + elementTypeRef.name.name + "]", psi) {

}

class CAnonMapTypeName(csf: CSchemaFile, override val psi: SchemaAnonMap)(implicit ctx: CContext) extends {


  val keyTypeRef: CTypeRef = new CTypeRef(csf, psi.getTypeRefList.head)

  val valueTypeRef: CTypeRef = new CTypeRef(csf, psi.getTypeRefList.last)

} with CTypeName(csf, "map[" + keyTypeRef.name.name + "," + valueTypeRef.name.name + "]", psi) {

//  if (keyTypeRef.name.name == "epigraph.String") {
//    val lnu = new LineNumberUtil(csf.psi.getText, 4)
//    val off = psi.getTextRange.getStartOffset
//    println("Error: " + lnu.line(off) + ":" + lnu.column(off))
//  } else println("KOOL")

}

trait CType {

  val name: CTypeName

}

class CTypeDef(val csf: CSchemaFile, val psi: SchemaTypeDef)(implicit val ctx: CContext) extends CType {

  val name: CTypeFqn = new CTypeFqn(csf, csf.namespace.fqn, psi)

  val declaredSupertypeRefs: Seq[CTypeRef] = {
    @Nullable val sed: SchemaExtendsDecl = psi.getExtendsDecl
    if (sed == null) Nil else sed.getFqnTypeRefList.map(new CTypeRef(csf, _))
  }

  val declaredSupplementees: Seq[CTypeRef] = {
    @Nullable val ssd: SchemaSupplementsDecl = psi.getSupplementsDecl
    if (ssd == null) Nil else ssd.getFqnTypeRefList.map(new CTypeRef(csf, _))
  }

  val injectedSupertypes: ConcurrentLinkedQueue[CTypeDef] = new ConcurrentLinkedQueue

  private var cachedSupertypes: Option[Seq[CTypeDef]] = None

  def supertypes: Seq[CTypeDef] = cachedSupertypes.getOrElse(Nil)

  def computeSupertypes(visited: mutable.Stack[CTypeDef]): Unit = {
    if (cachedSupertypes.isEmpty) {
      if (visited.contains(this)) {
        ctx.errors.add(
          new CError(
            csf.filename, csf.position(psi),
            s"Cyclic inheritance involving type '${name.name}': ${visited.map(_.name.name).mkString("->")}"
          )
        )
      } else {
        visited.push(this)
        val parents = declaredSupertypeRefs.map(_.resolved./*FIXME*/ asInstanceOf[CTypeDef]) ++ injectedSupertypes
        parents foreach {
          _.computeSupertypes(visited)
        }
        cachedSupertypes = Some(parents.flatMap { st => st +: st.supertypes }.distinct)
        visited.pop()
      }
    }
  }

}

object CTypeDef {

  def apply(csf: CSchemaFile, stdw: SchemaTypeDefWrapper)(implicit ctx: CContext): CTypeDef = {
    val std: SchemaTypeDef = stdw.getElement
    val ctype = std match {
      case vt: SchemaVarTypeDef => new CVarTypeDef(csf, vt)
      case rt: SchemaRecordTypeDef => new CRecordTypeDef(csf, rt)
      case mt: SchemaMapTypeDef => new CMapTypeDef(csf, mt)
      case lt: SchemaListTypeDef => new CListTypeDef(csf, lt)
      case et: SchemaEnumTypeDef => new CEnumTypeDef(csf, et)
      case pt: SchemaPrimitiveTypeDef => new CPrimitiveTypeDef(csf, pt)
      case _ => null
    }
    ctype
  }

}

class CVarTypeDef(csf: CSchemaFile, override val psi: SchemaVarTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi
) {

  val declaredTags: Seq[CTag] = {
    @Nullable val body = psi.getVarTypeBody
    if (body == null) Nil else body.getVarTagDeclList.map(new CTag(csf, _)).toList
  }

}

class CTag(val csf: CSchemaFile, val psi: SchemaVarTagDecl)(implicit val ctx: CContext) {

  val name: String = psi.getQid.getCanonicalName

  val typeRef: CTypeRef = new CTypeRef(csf, psi.getTypeRef)

}


class CRecordTypeDef(csf: CSchemaFile, override val psi: SchemaRecordTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi
) {

  val declaredFields: Seq[CField] = {
    @Nullable val body = psi.getRecordTypeBody
    if (body == null) Nil else body.getFieldDeclList.map(new CField(csf, _)).toList
  }

}

class CField(val csf: CSchemaFile, val psi: SchemaFieldDecl)(implicit val ctx: CContext) {

  val name: String = psi.getQid.getCanonicalName

  val typeRef: CTypeRef = new CTypeRef(csf, psi.getTypeRef)

}

trait CMapType extends CType {

  val name: CTypeName

  val keyTypeRef: CTypeRef

  val valueTypeRef: CTypeRef

}

class CAnonMapType(override val name: CAnonMapTypeName) extends CMapType {

  override val keyTypeRef: CTypeRef = name.keyTypeRef

  override val valueTypeRef: CTypeRef = name.valueTypeRef

}


class CMapTypeDef(csf: CSchemaFile, override val psi: SchemaMapTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi
) with CMapType {

  override val keyTypeRef: CTypeRef = new CTypeRef(csf, psi.getAnonMap.getTypeRefList.head)

  override val valueTypeRef: CTypeRef = new CTypeRef(csf, psi.getAnonMap.getTypeRefList.last)

}

trait CListType extends CType {

  val name: CTypeName

  val elementTypeRef: CTypeRef

}


class CAnonListType(override val name: CAnonListTypeName) extends CListType {

  override val elementTypeRef: CTypeRef = name.elementTypeRef

}


class CListTypeDef(csf: CSchemaFile, override val psi: SchemaListTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi
) with CListType {

  override val elementTypeRef: CTypeRef = new CTypeRef(csf, psi.getAnonList.getTypeRef)

}


class CEnumTypeDef(csf: CSchemaFile, psi: SchemaEnumTypeDef)(implicit ctx: CContext) extends CTypeDef(csf, psi) {

  val values: Seq[CEnumValue] = {
    @Nullable val body = psi.getEnumTypeBody
    if (body == null) Nil else body.getEnumMemberDeclList.map(new CEnumValue(csf, _)).toList
  }

}

class CEnumValue(csf: CSchemaFile, psi: SchemaEnumMemberDecl)(implicit val ctx: CContext) {

  val name: String = psi.getQid.getCanonicalName

}


class CPrimitiveTypeDef(csf: CSchemaFile, override val psi: SchemaPrimitiveTypeDef)
    (implicit ctx: CContext) extends CTypeDef(
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
