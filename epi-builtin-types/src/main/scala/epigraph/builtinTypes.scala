/* Created by yegor on 5/23/16. */

package epigraph

import com.sumologic.epigraph.xp._

trait StringPrimitive extends StringDatum[StringPrimitive]


object StringPrimitive extends StringType[StringPrimitive](ns \ "String")


trait IntegerPrimitive extends IntegerDatum[IntegerPrimitive]


object IntegerPrimitive extends IntegerType[IntegerPrimitive](ns \ "Integer")


trait LongPrimitive extends LongDatum[LongPrimitive]


object LongPrimitive extends LongType[LongPrimitive](ns \ "Long")


trait DoublePrimitive extends DoubleDatum[DoublePrimitive]


object DoublePrimitive extends DoubleType[DoublePrimitive](ns \ "Double")


trait BooleanPrimitive extends BooleanDatum[BooleanPrimitive]


object BooleanPrimitive extends BooleanType[BooleanPrimitive](ns \ "Boolean")
