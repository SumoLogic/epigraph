/* Created by yegor on 5/3/16. */

package com.sumologic.epigraph.raw

import com.sumologic.epigraph.core
import com.sumologic.epigraph.schema.SchemaSchema

trait ReadOnlyData extends core.Data {this: core.Names with core.Types =>


  class Value(override val valueType: VarType, override val data: Map[TypeMember, Datum]) extends ValueApi


  class Datum(override val dataType: DataType) extends DatumApi


  class RecordDatum(override val dataType: RecordDataType, override val fieldValues: Map[Field, Value]) extends Datum(
    dataType
  ) with RecordDatumApi


  class UnionDatum(override val dataType: UnionDataType, override val tagValues: Map[Tag, Value]) extends Datum(
    dataType
  ) with UnionDatumApi


  class MapDatum(override val dataType: MapDataType, override val entries: Map[Datum, Value]) extends Datum(
    dataType
  ) with MapDatumApi


  class ListDatum(override val dataType: ListDataType, override val elements: Seq[Value]) extends Datum(
    dataType
  ) with ListDatumApi


  class EnumDatum(override val dataType: EnumDataType, override val value: EnumTypeMember) extends Datum(
    dataType
  ) with EnumDatumApi


  abstract class PrimitiveDatum(
      override val dataType: PrimitiveDataType,
      override val native: NativePrimitive
  ) extends Datum(dataType) with PrimitiveDatumApi


  class StringDatum(
      override val dataType: StringDataType,
      override val native: NativeString
  ) extends PrimitiveDatum(dataType, native) with StringDatumApi

  // TODO other primitive types

  override type NativePrimitive = AnyRef

  override type NativeString = String

}


object ReadOnlyDataMain extends SchemaSchema with ReadOnlyData {

  def main(args: Array[String]) {
    val r = new RecordDatum(TypeType, Map.empty)
    println(r)
  }

}
