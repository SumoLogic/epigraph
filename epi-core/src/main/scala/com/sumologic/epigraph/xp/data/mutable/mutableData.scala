/* Created by yegor on 5/27/16. */

package com.sumologic.epigraph.xp.data.mutable

import com.sumologic.epigraph.names.TypeMemberName
import com.sumologic.epigraph.xp.data._
import com.sumologic.epigraph.xp.types._

trait MuVar[+M <: Var[M]] extends Var[M] {

  def getEntry[N <: TypeMemberName, T <: Datum[T]](tag: VarTag[_ >: M, N, T]): Option[MuVarEntry[_ <: M, N, T]]

}


trait MuMonoVar[+T <: Datum[T]] extends MonoVar[T] with MuVar[T] {

  //def getEntry[VT >: T <: Datum](tag: VarTag[VT]): Option[VarEntry[VT]]

}


trait MuVarEntry[M <: Var[M], N <: TypeMemberName, T <: Datum[T]] extends VarEntry[M, N, T] { // TODO variance?

  def tag: VarTag[M, N, T]

  def dataType: DataType[T]

  def data: T // TODO Option[T]? some other derivative?

  def error: Exception // TODO Throwable?

}


trait MuDatum[D <: Datum[D]] extends Datum[D] with MuMonoVar[D] {

}


trait MuRecordDatum[D <: MuRecordDatum[D]] extends RecordDatum[D] with MuDatum[D] {

  def set[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](field: TaggedField[_ >: D, M, N, T], datum: T): this.type = {
    set[M, N, T](field, field.tag, datum)
  }

  def set[M <: Var[M], N <: TypeMemberName, T <: Datum[T]](
      field: Field[_ >: D, M],
      varTag: VarTag[_ >: M, N, T],
      datum: T
  ): this.type

}


trait MuMapDatum[K <: Datum[K], M <: Var[M]] extends MapDatum[K, M] with MuDatum[MuMapDatum[K, M]] {

  def getVar(key: K): Option[M]

  //def getVarEntry[T <: Datum](key: K, varTag: VarTag[_ <: T]): Option[StaticVarEntry[_ >: M, T]]

}


trait MuTaggedMapDatum[K <: Datum[K], /*+*/ M <: Var[M], N <: TypeMemberName, T <: Datum[T]] extends TaggedMapDatum[K, M, N, T] with MuMapDatum[K, M] {

}


trait MuListDatum[M <: Var[M]] extends ListDatum[M] with MuDatum[MuListDatum[M]] {

}


trait MuTaggedListDatum[M <: Var[M], N <: TypeMemberName, V <: Datum[V]] extends TaggedListDatum[M, N, V] with MuListDatum[M] {

}

//trait MuEnumDatum[D <: MuEnumDatum[D]] extends EnumDatum[D] with MuDatum[D] {
//
//}

trait MuPrimitiveDatum[D <: MuPrimitiveDatum[D]] extends PrimitiveDatum[D] with MuDatum[D] {

  def set(native: PrimitiveDatum[D]#DatumType#Native) // TODO better type

}


trait MuStringDatum[D <: MuStringDatum[D]] extends StringDatum[D] with MuPrimitiveDatum[D]


trait MuIntegerDatum[D <: MuIntegerDatum[D]] extends IntegerDatum[D] with MuPrimitiveDatum[D]


trait MuLongDatum[D <: MuLongDatum[D]] extends LongDatum[D] with MuPrimitiveDatum[D]


trait MuDoubleDatum[D <: MuDoubleDatum[D]] extends DoubleDatum[D] with MuPrimitiveDatum[D]


trait MuBooleanDatum[D <: MuBooleanDatum[D]] extends BooleanDatum[D] with MuPrimitiveDatum[D]
