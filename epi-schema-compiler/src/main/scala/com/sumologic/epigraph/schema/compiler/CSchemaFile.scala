/* Created by yegor on 6/9/16. */

package com.sumologic.epigraph.schema.compiler

import com.sumologic.epigraph.schema.parser.Fqn
import com.sumologic.epigraph.schema.parser.psi.{SchemaVarTypeDef, _}
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._
import scala.collection.mutable

class CSchemaFile(val psi: SchemaFile)(implicit val ctx: CContext) {

  val namespace: CNamespace = new CNamespace(psi.getNamespaceDecl)

  val imports: Map[String, CImport] = psi.getImportStatements.map(new CImport(_)).map { ci =>
    (ci.alias, ci)
  }(collection.breakOut) // TODO deal with dupes (foo.Baz and bar.Baz)

  @Nullable
  private val defs: SchemaDefs = psi.getDefs

  val types: Iterable[CType] = if (defs == null) Nil else defs.getTypeDefWrapperList.map(CType.apply)

  def resolveLocalTypeRef(sftr: SchemaFqnTypeRef): CTypeFqn = {
    val fqn = sftr.getFqn.getFqn
    val alias = fqn.first
    val resolved = imports.get(alias).map(_.fqn.removeLastSegment().append(fqn).toString).getOrElse(
      namespace.fqn + "." + fqn.toString
    )
    new CTypeFqn(resolved)
  }

  class CNamespace(val psi: SchemaNamespaceDecl)(implicit val ctx: CContext) {

    val fqn: String = psi.getFqn2.toString
    // TODO expose custom attributes

  }

  class CImports(@Nullable val psi: SchemaImports)(implicit val ctx: CContext) {
    // explicit imports
    // implicit imports
    // file namespace?
  }

  class CImport(val psi: SchemaImportStatement)(implicit val ctx: CContext) {

    val fqn: Fqn = psi.getFqn.getFqn

    val alias: String = fqn.last

  }

  class CTypeRef(val psi: SchemaTypeRef)(implicit val ctx: CContext) {

    val name: CTypeName = (psi.getFqnTypeRef, psi.getAnonList, psi.getAnonMap) match {
      case (sftr, null, null) => resolveLocalTypeRef(sftr)
      case (null, sal, null) => new CListTypeName(sal)
      case (null, null, sam) => new CMapTypeName(sam)
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

    override def toString: String = "«" + name + "»"

  }


  class CTypeFqn(fqn: String)(implicit ctx: CContext) extends CTypeName(fqn) {

    def this(ns: String, ln: String) = this(ns + "." + ln)

  }

  class CListTypeName(psi: SchemaAnonList)(implicit ctx: CContext) extends {

    val elementTypeRef: CTypeRef = new CTypeRef(psi.getTypeRef)

  } with CTypeName("list[" + elementTypeRef.name.name + "]") {

  }

  class CMapTypeName(val psi: SchemaAnonMap)(implicit ctx: CContext) extends {

    val keyTypeRef: CTypeRef = new CTypeRef(psi.getTypeRefList.head)

    val valueTypeRef: CTypeRef = new CTypeRef(psi.getTypeRefList.last)

  } with CTypeName(
    "map[" + keyTypeRef.name.name + "," + valueTypeRef.name.name + "]"
  )


  class CType(val psi: SchemaTypeDef)(implicit val ctx: CContext) { // TODO hierarchy/constructors for anon list/map

    val name: CTypeFqn = new CTypeFqn(namespace.fqn, psi.getId.getText)

    val declaredSupertypeRefs: Seq[CTypeRef] = {
      @Nullable val sed: SchemaExtendsDecl = psi.getExtendsDecl
      if (sed == null) Nil else sed.getTypeRefList.map(new CTypeRef(_))
    }

    val injectedSupertypeRefs: mutable.Set[CTypeRef] = mutable.Set()

    override def toString: String = name + getClass.getSimpleName + "@" + Integer.toHexString(hashCode())

  }

  object CType {

    def apply(stdw: SchemaTypeDefWrapper)(implicit ctx: CContext): CType = {
      val std: SchemaTypeDef = stdw.getElement
      val ctype = std match {
        case vt: SchemaVarTypeDef => new CVarType(vt)
        case rt: SchemaRecordTypeDef => new CRecordType(rt)
        case mt: SchemaMapTypeDef => new CMapType(mt)
        case lt: SchemaListTypeDef => new CListType(lt)
        case et: SchemaEnumTypeDef => new CEnumType(et)
        case pt: SchemaPrimitiveTypeDef => new CPrimitiveType(pt)
        case _ => null
      }
      ctype
    }

  }

  class CVarType(override val psi: SchemaVarTypeDef)(implicit ctx: CContext) extends CType(psi) {

    val declaredTags: mutable.Map[String, CTag] = mutable.Map() // TODO populate from psi

  }

  class CTag(val name: String, val typeRef: CTypeRef) {

  }


  class CRecordType(override val psi: SchemaRecordTypeDef)(implicit ctx: CContext) extends CType(psi) {

    val declaredFields: mutable.Map[String, CField] = mutable.Map()

  }

  class CField(name: String, val typeRef: CTypeRef) {

  }


  class CMapType(override val psi: SchemaMapTypeDef)(implicit ctx: CContext) extends CType(psi) {

    val keyTypeRef: CTypeRef = ???

    val valueTypeRef: CTypeRef = ???

  }


  class CListType(override val psi: SchemaListTypeDef)(implicit ctx: CContext) extends {

  } with CType(psi) {

    val elementTypeRef: CTypeRef = ???

  }


  class CEnumType(psi: SchemaEnumTypeDef)(implicit ctx: CContext) extends CType(psi) {

    val values: mutable.Map[String, CEnumValue] = mutable.Map()

  }

  class CEnumValue(name: String)(implicit val ctx: CContext) {

  }


  class CPrimitiveType(override val psi: SchemaPrimitiveTypeDef)(implicit ctx: CContext) extends CType(psi) {

    val kind: CPrimitiveKind = CPrimitiveKind.forKeyword( // TODO deal with all nulls?
      Seq(psi.getStringT, psi.getIntegerT, psi.getLongT, psi.getDoubleT, psi.getBooleanT).find(_ != null).get.getText
    )

  }

}
