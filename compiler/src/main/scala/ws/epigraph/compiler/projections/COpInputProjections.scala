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
object COpInputProjections extends CGenProjections {
  override type VP = COpInputVarProjection
  override type TP = COpInputTagProjectionEntry

  override type MP = COpInputModelProjection[_ <: CType, _ <: CDatum]
  override type RMP = COpInputRecordModelProjection
  override type MMP = COpInputMapModelProjection
  override type LMP = COpInputListModelProjection
  override type PMP = CGenPrimitiveModelProjection

  override type FPE = CGenFieldProjectionEntry
  override type FP = CGenFieldProjection

  class COpInputVarProjection(
    val `type`: CEntityTypeDef,
    val tagProjections: Map[String, TP],
    val parenthesized: Boolean,
    val polymorphicTails: Option[List[VP]],
    val location: TextLocation
  ) extends CGenVarProjection

  class COpInputTagProjectionEntry(
    val tag: CTag,
    val projection: MP,
    val location: TextLocation
  ) extends CGenTagProjectionEntry

  class COpInputModelProjection[M <: CType, D <: CDatum](
    val model: M,
    val required: Boolean,
    val defaultValue: Option[D],
    val params: COpParams,
    val annotations: Annotations,
    val metaProjection: Option[MP],
    val location: TextLocation
  ) extends CGenModelProjection[M]

  class COpInputRecordModelProjection(
    model: CRecordTypeDef,
    required: Boolean,
    defaultValue: Option[CRecordDatum],
    params: COpParams,
    annotations: Annotations,
    metaProjection: Option[MP],
    val fieldProjections: Map[String, FPE],
    location: TextLocation
  ) extends COpInputModelProjection[CRecordTypeDef, CRecordDatum](
    model, required, defaultValue, params, annotations, metaProjection, location
  ) with CGenRecordModelProjection

  class COpInputFieldProjectionEntry(
    val field: CField,
    val fieldProjection: FP,
    val location: TextLocation
  ) extends CGenFieldProjectionEntry

  class COpInputFieldProjection(
    val params: COpParams,
    val annotations: Annotations,
    val varProjection: VP,
    val required: Boolean,
    val location: TextLocation
  ) extends CGenFieldProjection

  class COpInputMapModelProjection(
    model: CMapType,
    required: Boolean,
    defaultValue: Option[CMapDatum],
    params: COpParams,
    annotations: Annotations,
    metaProjection: Option[MP],
    val keyProjection: COpInputKeyProjection,
    val itemsProjection: VP,
    location: TextLocation
  ) extends COpInputModelProjection[CMapType, CMapDatum](
    model, required, defaultValue, params, annotations, metaProjection, location
  ) with CGenMapModelProjection

  class COpInputKeyProjection(
    val presence: COpKeyPresence,
    val params: COpParams,
    val annotations: Annotations,
    val location: TextLocation
  ) {
    def canEqual(other: Any): Boolean = other.isInstanceOf[COpInputKeyProjection]

    override def equals(other: Any): Boolean = other match {
      case that: COpInputKeyProjection =>
        (that canEqual this) && presence == that.presence && params == that.params && annotations == that.annotations
      case _ => false
    }

    override def hashCode(): Int = {
      val state = Seq(presence, params, annotations)
      state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }
  }

  class COpInputListModelProjection(
    model: CListType,
    required: Boolean,
    defaultValue: Option[CListDatum],
    params: COpParams,
    annotations: Annotations,
    metaProjection: Option[MP],
    val itemsProjection: VP,
    location: TextLocation
  ) extends COpInputModelProjection[CListType, CListDatum](
    model, required, defaultValue, params, annotations, metaProjection, location
  ) with CGenListModelProjection

  class COpInputPrimitiveModelProjection(
    model: CPrimitiveTypeDef,
    required: Boolean,
    defaultValue: Option[CPrimitiveDatum],
    params: COpParams,
    annotations: Annotations,
    metaProjection: Option[MP],
    location: TextLocation
  ) extends COpInputModelProjection[CPrimitiveTypeDef, CPrimitiveDatum](
    model, required, defaultValue, params, annotations, metaProjection, location
  ) with CGenPrimitiveModelProjection

}
