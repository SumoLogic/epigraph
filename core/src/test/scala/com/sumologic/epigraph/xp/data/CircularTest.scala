/* Created by yegor on 5/26/16. */

package com.sumologic.epigraph.xp.data

import com.sumologic.epigraph.names.QualifiedNamespaceName
import com.sumologic.epigraph.xp.types.RecordType

import scala.collection.{mutable => mut}

object ns extends QualifiedNamespaceName(None, "test")


object Log {

  private val stack = mut.Stack[String]()

  def push(ctx: String): Unit = {
    println("  " * stack.size + "+" + ctx)
    stack.push(ctx)
  }

  def pop(ctx: String): Unit = {
    assert(ctx == stack.pop())
    println("  " * stack.size + "-" + ctx)
  }

}


trait FooRecord extends RecordDatum[FooRecord]


object FooRecord extends RecordType[FooRecord](ns \ "FooRecord") {

  Log.push("FooRecord")

  val barList: DatumField[ListDatum[BarRecord]] = {
    Log.push("FooRecord.barList")
    val f = field("barList", BarRecord.listOf)
    Log.pop("FooRecord.barList")
    f
  }

  val bar: DatumField[BarRecord] = {
    Log.push("FooRecord.bar")
    val f = field("bar", BarRecord)
    Log.pop("FooRecord.bar")
    f
  }

  override val declaredFields: DeclaredFields = DeclaredFields(bar)

  Log.pop("FooRecord")

}


trait BarRecord extends RecordDatum[BarRecord]


object BarRecord extends RecordType[BarRecord](ns \ "BarRecord") {

  Log.push("BarRecord")

  val fooBarMap: DatumField[MapDatum[FooRecord, BarRecord]] = {
    Log.push("BarRecord.fooBarMap")
    val f = field("fooBarMap", BarRecord.mapBy(FooRecord))
    Log.pop("BarRecord.fooBarMap")
    f
  }

  val foo: DatumField[FooRecord] = {
    Log.push("BarRecord.foo")
    val f = field("foo", FooRecord)
    Log.pop("BarRecord.foo")
    f
  }

  val barList: DatumField[ListDatum[BarRecord]] = {
    Log.push("BarRecord.barList")
    val f = field("barList", BarRecord.listOf)
    Log.pop("BarRecord.barList")
    f
  }

  override val declaredFields: DeclaredFields = DeclaredFields(foo)

  Log.pop("BarRecord")

}


object CircularTest {

  def main(args: Array[String]) {
    println(FooRecord)
    println(FooRecord.bar)
    println(BarRecord)
    println(BarRecord.foo)
  }

}
