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

package ws.epigraph.compiler.projections

import ws.epigraph.compiler._
import ws.epigraph.lang.TextLocation

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class CGenProjections {
  type VP <: CGenVarProjection
  type TP <: CGenTagProjectionEntry

  type MP <: CGenModelProjection[_ <: CType]
  type RMP <: CGenRecordModelProjection
  type MMP <: CGenMapModelProjection
  type LMP <: CGenListModelProjection
  type PMP <: CGenPrimitiveModelProjection

  type FPE <: CGenFieldProjectionEntry
  type FP <: CGenFieldProjection

  trait CGenVarProjection {
    def `type`: CEntityTypeDef
    def tagProjections: Map[String, TP]
    def parenthesized: Boolean
    def polymorphicTails: Option[List[VP]]
    def location: TextLocation

    def validate(rep: ErrorReporter): Unit = {
      validateTags(rep)
      validateTails(rep)
    }

    private def validateTags(rep: ErrorReporter) = {
      if (!parenthesized && tagProjections.size != 1)
        rep.error(s"Non-parenthesized entity projection can only contain one tag; was passed ${tagProjections.size} tags",
          location)

      for ((tagName, tagProjection) <- tagProjections) {
        if (!`type`.effectiveTags.exists(t => t.name == tagName))
          rep.error(s"Tag '$tagName' does not belong to var type '${`type`.name.fqn.toString}'", tagProjection.location)

        val tagType: CType = tagProjection.tag.typeRef.resolved
        val tagProjectionModel: CType = tagProjection.projection.model

        if (!tagType.isAssignableFrom(tagProjectionModel))
          rep.error(s"Tag '$tagName' projection type '${tagProjectionModel.name.name}' is not a subtype of tag type '${tagType.name.name}'",
            tagProjection.location)
      }
    }

    private def validateTails(rep: ErrorReporter) = polymorphicTails.foreach{ tails =>
      tails.foreach{ tail =>
        if (!`type`.isAssignableFrom(tail.`type`))
          rep.error(s"Tail type '${tail.`type`.name.name}' is not a sub-type of var type '${`type`.name.name}'",
            tail.location)
      }
      // todo perform full normalization for all possible tail types and check for clashes
    }
  }

  trait CGenTagProjectionEntry {
    def tag: CTag
    def projection: MP
    def location: TextLocation
  }

  trait CGenModelProjection[M <: CType] {
    def model: M
    def metaProjection: Option[MP] // todo: Java projections have this wrong
    def annotations: Annotations
    def location: TextLocation

    def asRecord: RMP = this.asInstanceOf[RMP]
    def asMap: MMP = this.asInstanceOf[MMP]
    def asList: LMP = this.asInstanceOf[LMP]
    def asPrimitive: PMP = this.asInstanceOf[PMP]
  }

  trait CGenRecordModelProjection extends CGenModelProjection[CRecordTypeDef] {
    def fieldProjections: Map[String, FPE]
    def fieldProjection(fieldName: String): Option[FPE] = fieldProjections.get(fieldName)
  }

  trait CGenFieldProjectionEntry {
    def field: CField
    def fieldProjection: FP
    def location: TextLocation
  }

  trait CGenFieldProjection {
    def annotations: Annotations
    def varProjection: VP
    def location: TextLocation
  }

  trait CGenMapModelProjection extends CGenModelProjection[CMapType] {
    def itemsProjection: VP
  }

  trait CGenListModelProjection extends CGenModelProjection[CListType] {
    def itemsProjection: VP
  }

  trait CGenPrimitiveModelProjection extends CGenModelProjection[CPrimitiveTypeDef]

}
