/* Created by yegor on 6/3/16. */

package com.sumologic.epigraph.xp.data.immutable

import com.sumologic.epigraph.names._
import com.sumologic.epigraph.xp.data._
import com.sumologic.epigraph.xp.data.immutable._
import com.sumologic.epigraph.xp.data.mutable._
import com.sumologic.epigraph.xp.types._

object PrimitivesTest {

  val ns: QualifiedNamespaceName = QualifiedNamespaceName(None, "example")


  trait Foo extends StringDatum[Foo]


  trait ImmFoo extends ImmStringDatum[Foo] with Foo


  trait MutFoo extends MutStringDatum[Foo] with Foo


  object Foo extends StringType[Foo](ns \ "Foo") {

    override def createImmutable(native: String): ImmFoo = new ImmFooImpl(native)


    private class ImmFooImpl(native: String) extends ImmStringDatumImpl[Foo](this, native) with ImmFoo


    override def createMutable(native: String): MutFoo = new MutFooImpl(native)


    private class MutFooImpl(native: String) extends MutStringDatumImpl[Foo](this, native) with MutFoo


  }


  trait Bar extends Foo with StringDatum[Bar]


  trait ImmBar extends ImmFoo with ImmStringDatum[Bar] with Bar


  trait MutBar extends MutFoo with MutStringDatum[Bar] with Bar


  object Bar extends StringType[Bar](ns \ "Bar", Seq(Foo)) {

    override def createImmutable(native: String): ImmBar = new ImmBarImpl(native)


    private class ImmBarImpl(native: String) extends ImmStringDatumImpl[Bar](this, native) with ImmBar


    override def createMutable(native: String): MutBar = new MutBarImpl(native)


    private class MutBarImpl(native: String) extends MutStringDatumImpl[Bar](this, native) with MutBar


  }


  def main(args: Array[String]) {

    val ifoo: ImmFoo = Foo.createImmutable("foo")
    val ifooval: String = ifoo.native
    println(ifoo)

    val mfoo: MutFoo = Foo.createMutable("bar")
    val mfooval: String = mfoo.native
    println(mfoo)

    mfoo.native = "baz"
    println(mfoo)

    val ibar: ImmFoo = Bar.createImmutable("foo")
    val ibarval: String = ibar.native
    println(ibar)

    val mbar: MutFoo = Bar.createMutable("bar")
    val mbarval: String = mbar.native
    println(mbar)

    mbar.native = "baz"
    println(mbar)

  }

}
