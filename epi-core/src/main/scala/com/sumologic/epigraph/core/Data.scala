/* Created by yegor on 4/25/16. */

package com.sumologic.epigraph.core

trait Data {this: Types =>

  type Value >: Null <: AnyRef with ValueApi

  type Datum >: Null <: AnyRef with DatumApi

  type RecordDatum >: Null <: Datum with RecordDatumApi

  type UnionDatum >: Null <: Datum with UnionDatumApi

  type MapDatum >: Null <: Datum with MapDatumApi

  type ListDatum >: Null <: Datum with ListDatumApi

  type EnumDatum >: Null <: Datum with EnumDatumApi

  type PrimitiveDatum >: Null <: Datum with PrimitiveDatumApi

  type PrimitiveNativeValue >: Null <: AnyRef // AnyVal is not allowed; reconsider?

  type StringDatum >: Null <: PrimitiveDatum with StringDatumApi

  type StringNativeValue >: Null <: PrimitiveNativeValue

  // TODO other primitive data

  trait ValueApi {this: Value =>

    def valueType: VarType

    def data: Map[TypeMember, Datum]

  }


  trait DatumApi {this: Datum =>

    def dataType: DataType

  }


  trait RecordDatumApi extends DatumApi {this: RecordDatum =>

    override def dataType: RecordDataType

    def fieldValues: Map[Field, Value]

  }


  trait UnionDatumApi extends DatumApi {this: UnionDatum =>

    override def dataType: UnionDataType

    def tagValues: Map[Tag, Value]

  }


  trait MapDatumApi extends DatumApi {this: MapDatum =>

    override def dataType: MapDataType

    def entries: Map[Datum, Value]

  }


  trait ListDatumApi extends DatumApi {this: ListDatum =>

    override def dataType: ListDataType

    def elements: Seq[Value]

  }


  trait EnumDatumApi extends DatumApi {this: EnumDatum =>

    override def dataType: EnumDataType

    def value: EnumTypeMember

  }


  trait PrimitiveDatumApi extends DatumApi {this: PrimitiveDatum =>

    override def dataType: PrimitiveDataType

    def nativeValue: PrimitiveNativeValue

  }


  trait StringDatumApi extends PrimitiveDatumApi {this: StringDatum =>

    override def dataType: StringDataType

    override def nativeValue: StringNativeValue

  }

  // TODO other primitive data

}
