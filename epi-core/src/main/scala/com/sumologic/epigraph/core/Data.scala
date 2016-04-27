/* Created by yegor on 4/25/16. */

package com.sumologic.epigraph.core

trait Data {this: Nature =>

  type Value >: Null <: AnyRef with ValueApi


  trait ValueApi {this: Value =>

    def `type`: ValueType

    def data: Map[TypeMember, Datum]

  }


  type Datum >: Null <: AnyRef with DatumApi


  trait DatumApi {this: Datum =>

    def `type`: DataType

  }


  type RecordDatum >: Null <: Datum with RecordDatumApi


  trait RecordDatumApi extends DatumApi {this: RecordDatum =>

    override def `type`: RecordDataType

    def fieldValues: Map[Field, Value]

  }


  type UnionDatum >: Null <: Datum with UnionDatumApi


  trait UnionDatumApi extends DatumApi {this: UnionDatum =>

    override def `type`: UnionDataType

    def tagValues: Map[Tag, Value]

  }


  type MapDatum >: Null <: Datum with MapDatumApi


  trait MapDatumApi extends DatumApi {this: MapDatum =>

    override def `type`: MapDataType

    def entries: Map[Datum, Value]

  }


  type ListDatum >: Null <: Datum with ListDatumApi


  trait ListDatumApi extends DatumApi {this: ListDatum =>

    override def `type`: ListDataType

    def elements: Seq[Value]

  }


  type EnumDatum >: Null <: Datum with EnumDatumApi


  trait EnumDatumApi extends DatumApi {this: EnumDatum =>

    override def `type`: EnumDataType

    def value: EnumTypeMember

  }


  type PrimitiveDatum >: Null <: Datum with PrimitiveDatumApi


  trait PrimitiveDatumApi extends DatumApi {this: PrimitiveDatum =>

    override def `type`: PrimitiveDataType

    def nativeValue: PrimitiveNativeValue

  }


  type PrimitiveNativeValue >: Null <: AnyRef // AnyVal is not allowed - reconsider?

  type StringDatum >: Null <: PrimitiveDatum with StringDatumApi


  trait StringDatumApi extends PrimitiveDatumApi {this: StringDatum =>

    override def `type`: StringDataType

    override def nativeValue: StringNativeValue

  }


  type StringNativeValue >: Null <: PrimitiveNativeValue

  // TODO other primitive data

}
