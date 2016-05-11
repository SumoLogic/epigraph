/* Created by yegor on 4/25/16. */

package com.sumologic.epigraph.gen

trait GenData {this: GenTypes => // TODO rename to ReadableData?

  type GenVar >: Null <: AnyRef with VarApi // TODO GenVar?

  type GenDatum >: Null <: AnyRef with DatumApi

  type GenRecordDatum >: Null <: GenDatum with RecordDatumApi

  type GenUnionDatum >: Null <: GenDatum with UnionDatumApi

  type GenMapDatum >: Null <: GenDatum with MapDatumApi

  type GenListDatum >: Null <: GenDatum with ListDatumApi

  type GenEnumDatum >: Null <: GenDatum with EnumDatumApi

  type GenPrimitiveDatum >: Null <: GenDatum with PrimitiveDatumApi

  type NativePrimitive >: Null <: AnyRef // AnyVal is not allowed; reconsider?

  type GenStringDatum >: Null <: GenPrimitiveDatum with StringDatumApi

  type NativeString >: Null <: NativePrimitive // TODO move Native* to orthogonal trait?

  // TODO other primitive data

  trait VarApi {this: GenVar =>

    def varType: GenVarType

    def data: Map[GenTypeMember, GenDatum]

  }


  trait DatumApi {this: GenDatum =>

    def dataType: GenDataType

  }


  trait RecordDatumApi extends DatumApi {this: GenRecordDatum =>

    override def dataType: GenRecordType

    def fieldValues: Map[GenField, GenVar]

  }


  trait UnionDatumApi extends DatumApi {this: GenUnionDatum => // TODO add UnionValueApi or ValueApi or add `def value: GenValue`

    override def dataType: GenUnionType

    def tag: GenTag

    def value: GenVar

  }


  trait MapDatumApi extends DatumApi {this: GenMapDatum =>

    override def dataType: GenMapType

    def entries: Map[GenDatum, GenVar]

  }


  trait ListDatumApi extends DatumApi {this: GenListDatum =>

    override def dataType: GenListType

    def elements: Seq[GenVar]

  }


  trait EnumDatumApi extends DatumApi {this: GenEnumDatum =>

    override def dataType: GenEnumType

    def value: GenEnumTypeMember

  }


  trait PrimitiveDatumApi extends DatumApi {this: GenPrimitiveDatum =>

    override def dataType: GenPrimitiveType

    def native: NativePrimitive

  }


  trait StringDatumApi extends PrimitiveDatumApi {this: GenStringDatum =>

    override def dataType: GenStringType

    override def native: NativeString

  }

  // TODO other primitive data

}
