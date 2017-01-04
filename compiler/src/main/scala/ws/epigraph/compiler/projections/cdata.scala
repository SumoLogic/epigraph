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
import scala.collection.Map

abstract class CDataValue(val location: TextLocation)

class CData(
  val `type`: Option[CDataType],
  val tags: Map[String, CDatum],
  location: TextLocation
) extends CDataValue(location) {

  def canEqual(other: Any): Boolean = other.isInstanceOf[CData]

  override def equals(other: Any): Boolean = other match {
    case that: CData => (that canEqual this) && `type` == that.`type` && tags == that.tags
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(`type`, tags)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

abstract class CDatum(
  val `type`: Option[CDatumType],
  location: TextLocation
) extends CDataValue(location) {

  def canEqual(other: Any): Boolean = other.isInstanceOf[CDatum]

  override def equals(other: Any): Boolean = other match {
    case that: CDatum => (that canEqual this) && `type` == that.`type`
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(`type`)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class CRecordDatum(
  `type`: Option[CRecordTypeDef],
  val fields: Map[String, CDataValue],
  location: TextLocation
) extends CDatum(`type`, location) {

  override def canEqual(other: Any): Boolean = other.isInstanceOf[CRecordDatum]

  override def equals(other: Any): Boolean = other match {
    case that: CRecordDatum => super.equals(that) && (that canEqual this) && fields == that.fields
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(super.hashCode(), fields)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class CMapDatum(
  `type`: Option[CMapType],
  val entries: Map[CDatum, CDataValue],
  location: TextLocation
) extends CDatum(`type`, location) {

  override def canEqual(other: Any): Boolean = other.isInstanceOf[CMapDatum]

  override def equals(other: Any): Boolean = other match {
    case that: CMapDatum => super.equals(that) && (that canEqual this) && entries == that.entries
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(super.hashCode(), entries)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class CListDatum(
  `type`: Option[CListType],
  val values: List[CDataValue],
  location: TextLocation
) extends CDatum(`type`, location) {

  override def canEqual(other: Any): Boolean = other.isInstanceOf[CListDatum]

  override def equals(other: Any): Boolean = other match {
    case that: CListDatum => super.equals(that) && (that canEqual this) && values == that.values
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(super.hashCode(), values)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class CPrimitiveDatum(
  `type`: Option[CPrimitiveTypeDef],
  val value: Any,
  location: TextLocation
) extends CDatum(`type`, location) {

  override def canEqual(other: Any): Boolean = other.isInstanceOf[CPrimitiveDatum]

  override def equals(other: Any): Boolean = other match {
    case that: CPrimitiveDatum => super.equals(that) && (that canEqual this) && value == that.value
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(super.hashCode(), value)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class CNullDatum(
  `type`: Option[CDatumType],
  location: TextLocation
) extends CDatum(`type`, location)
