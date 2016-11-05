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

/* Created by yegor on 5/6/16. */

package ws.epigraph.med

import ws.epigraph.gen.{GenData, GenNames}

trait MedData extends GenData {this: GenNames with MedTypes =>

  override type GenVar = Var[_]

  override type GenDatum = Datum[_]

  override type GenRecordDatum = RecordDatum[_]

//  override type GenUnionDatum = UnionDatum[_]
//
//  override type GenMapDatum = MapDatum[_]

  override type GenListDatum = ListDatum[_, _]

//  override type GenEnumDatum = EnumDatum[_]

  override type GenPrimitiveDatum = PrimitiveDatum[_]

  override type GenStringDatum = StringDatum[_]

  override type NativePrimitive = AnyRef

  override type NativeString = String


  trait Var[+V <: Var[V]] extends VarApi {

    override def varType: VarType[_ <: V]

    override def data: Map[TypeMember[_], Datum[_]]

  }


  trait VarWithDefault[+V <: VarWithDefault[V, D], +D <: Datum[D]] extends Var[V] {

    def default: D // TODO some derivative of D (e.g. Option[D], Future[D], etc.)

  }


  trait Datum[+D <: Datum[D]] extends Var[D] with DatumApi {this: D =>}


  trait RecordDatum[+RD <: RecordDatum[RD]] extends Datum[RD] with RecordDatumApi {this: RD =>

    override def dataType: RecordType[_ <: RD] = ???

    override def fieldValues: Map[Field[_, _], Var[_]] = ???

    // TODO return some derivative of D, e.g. Option[D], Future[D], etc.?
    def get[V <: Var[V], D <: Datum[D]](field: Field[_ >: RD, V]): D

  }

  // TODO UnionDatum
  // TODO MapDatum

  trait ListDatum[+D <: ListDatum[D, V], V <: Var[V]] extends Datum[D] with ListDatumApi {this: D =>

    override def dataType: ListType[_ <: D, V]

    override def elements: Seq[Var[V]]

  }

  // TODO EnumDatum

  trait PrimitiveDatum[+D <: PrimitiveDatum[D]] extends Datum[D] with PrimitiveDatumApi {this: D =>}


  trait StringDatum[+D <: StringDatum[D]] extends PrimitiveDatum[D] with StringDatumApi {this: D =>}

  // TODO other primitives

}
