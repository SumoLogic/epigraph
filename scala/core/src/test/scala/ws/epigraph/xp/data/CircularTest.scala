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

/* Created by yegor on 5/26/16. */

package ws.epigraph.xp.data

import ws.epigraph.names.QualifiedNamespaceName
import ws.epigraph.xp.types.RecordType

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
