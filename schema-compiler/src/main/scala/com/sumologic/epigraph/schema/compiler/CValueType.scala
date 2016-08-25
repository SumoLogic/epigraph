/* Created by yegor on 8/23/16. */

package com.sumologic.epigraph.schema.compiler

import io.epigraph.lang.schema.parser.psi.SchemaValueTypeRef

/**
 * Schema value (field value, list element, or map value) type.
 *
 * @param csf [[CSchemaFile]] where value type is specified
 * @param psi [[SchemaValueTypeRef]] PSI element
 * @param ctx [[CContext]] compiler context
 */
class CValueType(val csf: CSchemaFile, psi: SchemaValueTypeRef)(implicit val ctx: CContext) {

  val polymorphic: Boolean = psi.getPolymorphic ne null

  val typeRef: CTypeRef = CTypeRef(csf, psi.getTypeRef)

  val declaredDefaultTagName: Option[Option[String]] = CTypeDef.declaredDefaultTagName(psi.getDefaultOverride)

}
