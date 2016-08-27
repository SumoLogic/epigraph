/* Created by yegor on 8/23/16. */

package com.sumologic.epigraph.schema.compiler

import io.epigraph.lang.parser.psi.EpigraphValueTypeRef

/**
 * Schema value (field value, list element, or map value) type.
 *
 * @param csf [[CSchemaFile]] where value type is specified
 * @param psi [[EpigraphValueTypeRef]] PSI element
 * @param ctx [[CContext]] compiler context
 */
class CValueType(val csf: CSchemaFile, psi: EpigraphValueTypeRef)(implicit val ctx: CContext) {

  val polymorphic: Boolean = psi.getPolymorphic ne null

  val typeRef: CTypeRef = CTypeRef(csf, psi.getTypeRef)

  val defaultDeclarationOpt: Option[Option[String]] = CTypeDef.declaredDefaultTagName(psi.getDefaultOverride)

}
