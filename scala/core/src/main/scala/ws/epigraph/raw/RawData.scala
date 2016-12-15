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

/* Created by yegor on 5/3/16. */

package ws.epigraph.raw

import ws.epigraph.gen

trait RawData extends gen.GenData {this: gen.GenNames with gen.GenTypes =>

  override type GenVar = Var // TODO Var?

  override type GenDatum = Datum

  override type GenRecordDatum = RecordDatum

  override type GenUnionDatum = UnionDatum

  override type GenMapDatum = MapDatum

  override type GenListDatum = ListDatum

  override type GenEnumDatum = EnumDatum

  override type GenPrimitiveDatum = PrimitiveDatum

  override type GenStringDatum = StringDatum

  override type NativePrimitive = AnyRef

  override type NativeString = String


  class Var(override val varType: GenVarType, override val data: Map[GenTypeMember, Datum]) extends VarApi


  class Datum(override val dataType: GenDataType) extends DatumApi


  class RecordDatum(
      override val dataType: GenRecordType,
      override val fieldValues: Map[GenField, Var]
  ) extends Datum(
    dataType
  ) with RecordDatumApi


  class UnionDatum(
      override val dataType: GenUnionType,
      override val tag: GenTag,
      override val value: Var
  ) extends Datum(
    dataType
  ) with UnionDatumApi


  class MapDatum(override val dataType: GenMapType, override val entries: Map[Datum, Var]) extends Datum(
    dataType
  ) with MapDatumApi


  class ListDatum(override val dataType: GenListType, override val elements: Seq[Var]) extends Datum(
    dataType
  ) with ListDatumApi


  class EnumDatum(override val dataType: GenEnumType, override val value: GenEnumTypeMember) extends Datum(
    dataType
  ) with EnumDatumApi


  abstract class PrimitiveDatum(
      override val dataType: GenPrimitiveType,
      override val native: NativePrimitive
  ) extends Datum(dataType) with PrimitiveDatumApi


  class StringDatum(
      override val dataType: GenStringType,
      override val native: NativeString
  ) extends PrimitiveDatum(dataType, native) with StringDatumApi

  // TODO other primitive types

}

import ws.epigraph.edl.RawEdlEdl

object RawDataMain extends RawEdlEdl with RawData {

  def main(args: Array[String]) {
    val r = new RecordDatum(TypeType, Map.empty)
    println(r)
  }

}
