/* Created by yegor on 5/23/16. */

package epigraph

import ws.epigraph.xp.data._
import ws.epigraph.xp.data.immutable._
import ws.epigraph.xp.data.mutable._
import ws.epigraph.xp.types.{BooleanType, DoubleType, IntegerType, LongType, StringType}

trait StringPrimitive extends StringDatum[StringPrimitive]


trait ImmStringPrimitive extends ImmStringDatum[StringPrimitive] with StringPrimitive


trait MutStringPrimitive extends MutStringDatum[StringPrimitive] with StringPrimitive


object StringPrimitive extends StringType[StringPrimitive](ns \ "String") {

  override def createImmutable(native: String): ImmStringPrimitive = new ImmStringPrimitiveImpl(native)


  private class ImmStringPrimitiveImpl(native: String)
      extends ImmStringDatumImpl[StringPrimitive](this, native) with ImmStringPrimitive


  override def createMutable(native: String): MutStringPrimitive = new MutStringPrimitiveImpl(native)


  private class MutStringPrimitiveImpl(native: String)
      extends MutStringDatumImpl[StringPrimitive](this, native) with MutStringPrimitive


}


trait IntegerPrimitive extends IntegerDatum[IntegerPrimitive]


trait ImmIntegerPrimitive extends ImmIntegerDatum[IntegerPrimitive] with IntegerPrimitive


trait MutIntegerPrimitive extends MutIntegerDatum[IntegerPrimitive] with IntegerPrimitive


object IntegerPrimitive extends IntegerType[IntegerPrimitive](ns \ "Integer") {

  override def createImmutable(native: Int): ImmIntegerPrimitive = new ImmIntegerPrimitiveImpl(native)


  private class ImmIntegerPrimitiveImpl(native: Int) extends ImmIntegerDatumImpl(native) with ImmIntegerPrimitive


  override def createMutable(native: Int): MutIntegerPrimitive = new MutIntegerPrimitiveImpl(native)


  private class MutIntegerPrimitiveImpl(native: Int) extends MutIntegerDatumImpl(native) with MutIntegerPrimitive


}


trait LongPrimitive extends LongDatum[LongPrimitive]


trait ImmLongPrimitive extends ImmLongDatum[LongPrimitive] with LongPrimitive


trait MutLongPrimitive extends MutLongDatum[LongPrimitive] with LongPrimitive


object LongPrimitive extends LongType[LongPrimitive](ns \ "Long") {

  override def createImmutable(native: Long): ImmLongPrimitive = new ImmLongPrimitiveImpl(native)


  private class ImmLongPrimitiveImpl(native: Long)
      extends ImmLongDatumImpl[LongPrimitive](this, native) with ImmLongPrimitive


  override def createMutable(native: Long): MutLongPrimitive = new MutLongPrimitiveImpl(native)


  private class MutLongPrimitiveImpl(native: Long)
      extends MutLongDatumImpl[LongPrimitive](this, native) with MutLongPrimitive


}


trait DoublePrimitive extends DoubleDatum[DoublePrimitive]


trait ImmDoublePrimitive extends ImmDoubleDatum[DoublePrimitive] with DoublePrimitive


trait MutDoublePrimitive extends MutDoubleDatum[DoublePrimitive] with DoublePrimitive


object DoublePrimitive extends DoubleType[DoublePrimitive](ns \ "Double") {

  override def createImmutable(native: Double): ImmDoublePrimitive = new ImmDoublePrimitiveImpl(native)


  private class ImmDoublePrimitiveImpl(native: Double)
      extends ImmDoubleDatumImpl[DoublePrimitive](this, native) with ImmDoublePrimitive


  override def createMutable(native: Double): MutDoublePrimitive = new MutDoublePrimitiveImpl(native)


  private class MutDoublePrimitiveImpl(native: Double)
      extends MutDoubleDatumImpl[DoublePrimitive](this, native) with MutDoublePrimitive


}


trait BooleanPrimitive extends BooleanDatum[BooleanPrimitive]


trait ImmBooleanPrimitive extends ImmBooleanDatum[BooleanPrimitive] with BooleanPrimitive


trait MutBooleanPrimitive extends MutBooleanDatum[BooleanPrimitive] with BooleanPrimitive


object BooleanPrimitive extends BooleanType[BooleanPrimitive](ns \ "Boolean") {

  override def createImmutable(native: Boolean): ImmBooleanPrimitive = new ImmBooleanPrimitiveImpl(native)


  private class ImmBooleanPrimitiveImpl(native: Boolean)
      extends ImmBooleanDatumImpl[BooleanPrimitive](this, native) with ImmBooleanPrimitive


  override def createMutable(native: Boolean): MutBooleanPrimitive = new MutBooleanPrimitiveImpl(native)


  private class MutBooleanPrimitiveImpl(native: Boolean)
      extends MutBooleanDatumImpl[BooleanPrimitive](this, native) with MutBooleanPrimitive


}
