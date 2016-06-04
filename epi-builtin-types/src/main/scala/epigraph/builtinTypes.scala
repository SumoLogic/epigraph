/* Created by yegor on 5/23/16. */

package epigraph

import com.sumologic.epigraph.xp.data._
import com.sumologic.epigraph.xp.data.immutable._
import com.sumologic.epigraph.xp.types.{BooleanType, DoubleType, IntegerType, LongType, StringType}

trait StringPrimitive extends StringDatum[StringPrimitive]


trait ImmStringPrimitive extends ImmStringDatum[StringPrimitive] with StringPrimitive


object StringPrimitive extends StringType[StringPrimitive](ns \ "String") {

  override def createImmutable(native: String): ImmStringPrimitive = new ImmStringPrimitiveImpl(native)


  private class ImmStringPrimitiveImpl(override val native: String)
      extends ImmStringDatumImpl[StringPrimitive](this, native) with ImmStringPrimitive


}


trait IntegerPrimitive extends IntegerDatum[IntegerPrimitive]


trait ImmIntegerPrimitive extends ImmIntegerDatum[IntegerPrimitive] with IntegerPrimitive


object IntegerPrimitive extends IntegerType[IntegerPrimitive](ns \ "Integer") {

  override def createImmutable(native: Int): ImmIntegerPrimitive = new ImmIntegerPrimitiveImpl(native)


  private class ImmIntegerPrimitiveImpl(override val native: Integer)
      extends ImmIntegerDatumImpl[IntegerPrimitive](this, native) with ImmIntegerPrimitive


}


trait LongPrimitive extends LongDatum[LongPrimitive]


trait ImmLongPrimitive extends ImmLongDatum[LongPrimitive] with LongPrimitive


object LongPrimitive extends LongType[LongPrimitive](ns \ "Long") {

  override def createImmutable(native: Long): ImmLongPrimitive = new ImmLongPrimitiveImpl(native)


  private class ImmLongPrimitiveImpl(override val native: Long)
      extends ImmLongDatumImpl[LongPrimitive](this, native) with ImmLongPrimitive


}


trait DoublePrimitive extends DoubleDatum[DoublePrimitive]


trait ImmDoublePrimitive extends ImmDoubleDatum[DoublePrimitive] with DoublePrimitive


object DoublePrimitive extends DoubleType[DoublePrimitive](ns \ "Double") {

  override def createImmutable(native: Double): ImmDoublePrimitive = new ImmDoublePrimitiveImpl(native)


  private class ImmDoublePrimitiveImpl(override val native: Double)
      extends ImmDoubleDatumImpl[DoublePrimitive](this, native) with ImmDoublePrimitive


}


trait BooleanPrimitive extends BooleanDatum[BooleanPrimitive]


trait ImmBooleanPrimitive extends ImmBooleanDatum[BooleanPrimitive] with BooleanPrimitive


object BooleanPrimitive extends BooleanType[BooleanPrimitive](ns \ "Boolean") {

  override def createImmutable(native: Boolean): ImmBooleanPrimitive = new ImmBooleanPrimitiveImpl(native)


  private class ImmBooleanPrimitiveImpl(override val native: Boolean)
      extends ImmBooleanDatumImpl[BooleanPrimitive](this, native) with ImmBooleanPrimitive


}
