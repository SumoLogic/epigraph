/* Created by yegor on 6/3/16. */

package com.sumologic.epigraph.xp.data.immutable

import com.sumologic.epigraph.xp.data._
import com.sumologic.epigraph.xp.types._

import scala.util.{Success, Try}

class ImmVarEntry[+T <: Datum[T]](override val value: Try[T]) extends VarEntry[T]


trait ImmDatum[+D <: Datum[D]] extends Datum[D] {this: D =>

  val entry: ImmVarEntry[D] = new ImmVarEntry[D](Success(this))

  private lazy val someEntry: Option[ImmVarEntry[D]] = Some(entry)

  override def getEntry[T <: Datum[T]](tag: Tag[_ >: D, _, T]): Option[ImmVarEntry[T]] = {
    // FIXME check tag is ours or compatible with (i.e. supertype's)
    someEntry.asInstanceOf[Option[ImmVarEntry[T]]]
  }

}


trait ImmRecordDatum[+D <: RecordDatum[D]] extends RecordDatum[D] {this: D =>

}


trait ImmPrimitiveDatum[+D <: PrimitiveDatum[D]] extends ImmDatum[D] with PrimitiveDatum[D] {this: D =>}


abstract class ImmPrimitiveDatumImpl[+D <: PrimitiveDatum[D]](
    override val dataType: PrimitiveType[_ <: D]
) extends ImmDatum[D] with PrimitiveDatum[D] {this: D =>}


trait ImmStringDatum[+D <: StringDatum[D]] extends ImmPrimitiveDatum[D] with StringDatum[D] {this: D =>}


abstract class ImmStringDatumImpl[+D <: StringDatum[D]](
    override val dataType: StringType[_ <: D],
    override val native: String
) extends ImmPrimitiveDatumImpl[D](dataType) with ImmStringDatum[D] {this: D =>}


trait ImmIntegerDatum[+D <: IntegerDatum[D]] extends ImmPrimitiveDatum[D] with IntegerDatum[D] {this: D =>}


abstract class ImmIntegerDatumImpl[+D <: IntegerDatum[D]](
    override val dataType: IntegerType[_ <: D],
    override val native: Integer
) extends ImmPrimitiveDatumImpl[D](dataType) with ImmIntegerDatum[D] {this: D =>}


trait ImmLongDatum[+D <: LongDatum[D]] extends ImmPrimitiveDatum[D] with LongDatum[D] {this: D =>}


abstract class ImmLongDatumImpl[+D <: LongDatum[D]](
    override val dataType: LongType[_ <: D],
    override val native: Long
) extends ImmPrimitiveDatumImpl[D](dataType) with ImmLongDatum[D] {this: D =>}


trait ImmDoubleDatum[+D <: DoubleDatum[D]] extends ImmPrimitiveDatum[D] with DoubleDatum[D] {this: D =>}


abstract class ImmDoubleDatumImpl[+D <: DoubleDatum[D]](
    override val dataType: DoubleType[_ <: D],
    override val native: Double
) extends ImmPrimitiveDatumImpl[D](dataType) with ImmDoubleDatum[D] {this: D =>}


trait ImmBooleanDatum[+D <: BooleanDatum[D]] extends ImmPrimitiveDatum[D] with BooleanDatum[D] {this: D =>}


abstract class ImmBooleanDatumImpl[+D <: BooleanDatum[D]](
    override val dataType: BooleanType[_ <: D],
    override val native: Boolean
) extends ImmPrimitiveDatumImpl[D](dataType) with ImmBooleanDatum[D] {this: D =>}
