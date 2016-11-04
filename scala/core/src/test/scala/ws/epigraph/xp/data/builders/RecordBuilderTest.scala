/* Created by yegor on 6/8/16. */

package ws.epigraph.xp.data.builders

import ws.epigraph.names.QualifiedNamespaceName
import ws.epigraph.xp.data._
import ws.epigraph.xp.data.immutable._
import ws.epigraph.xp.data.mutable._
import ws.epigraph.xp.types._

object RecordBuilderTest {

  val ns: QualifiedNamespaceName = QualifiedNamespaceName(None, "example")


  trait FooId extends IntegerDatum[FooId]


  trait ImmFooId extends FooId with ImmIntegerDatum[FooId]


  trait MutFooId extends FooId with MutIntegerDatum[FooId]


  trait FooIdBuilder extends FooId with IntegerDatumBuilder[FooId]


  object FooId extends IntegerType[FooId](ns \ "FooId") {

    override def createImmutable(native: Int): ImmFooId = new ImmFooIdImpl(native)


    private class ImmFooIdImpl(native: Int) extends ImmIntegerDatumImpl(native) with ImmFooId


    override def createMutable(native: Int): MutFooId = new MutFooIdImpl(native)


    private class MutFooIdImpl(native: Int) extends MutIntegerDatumImpl(native) with MutFooId


    override def createBuilder: FooIdBuilder = createBuilder(NativeDefault)

    override def createBuilder(native: Native = NativeDefault): FooIdBuilder = new FooIdBuilderImpl(native)


    private class FooIdBuilderImpl(native: Native) extends IntegerDatumBuilderImpl(native) with FooIdBuilder


  }


  trait BarId extends FooId with IntegerDatum[BarId]


  trait ImmBarId extends ImmFooId with BarId with ImmIntegerDatum[BarId]


  trait MutBarId extends MutFooId with BarId with MutIntegerDatum[BarId]


  trait BarIdBuilder extends BarId with IntegerDatumBuilder[BarId]


  object BarId extends IntegerType[BarId](ns \ "BarId", Seq(FooId)) {

    override def createImmutable(native: Int): ImmBarId = new ImmBarIdImpl(native)


    private class ImmBarIdImpl(native: Int) extends ImmIntegerDatumImpl(native) with ImmBarId


    override def createMutable(native: Int): MutBarId = new MutBarIdImpl(native)


    private class MutBarIdImpl(native: Int) extends MutIntegerDatumImpl(native) with MutBarId


    override def createBuilder: BarIdBuilder = createBuilder(NativeDefault)

    override def createBuilder(native: Native): BarIdBuilder = new BarIdBuilderImpl(native)


    private class BarIdBuilderImpl(native: Native) extends IntegerDatumBuilderImpl(native) with BarIdBuilder


  }


  trait FooRecord extends RecordDatum[FooRecord]

//  trait ImmFooRecord extends FooRecord with ImmRecordDatum[FooRecord]

  trait MutFooRecord extends FooRecord with MutRecordDatum[FooRecord]


  trait FooRecordBuilder extends FooRecord with RecordDatumBuilder[FooRecord]


  object FooRecord extends RecordType[FooRecord](ns \ "FooRecord") {

    val _id: DatumField[FooId] = field("id", FooId)

    override def declaredFields: DeclaredFields = DeclaredFields(_id)

    override def createMutable: MutFooRecord = new MutFooRecord {
      override def dataType: FooRecord.type = FooRecord
    }

    override def createBuilder: FooRecordBuilder = new FooRecordBuilder {
      override def dataType: FooRecord.type = FooRecord
    }
  }


  def main(args: Array[String]) {
    val fr = FooRecord.createBuilder
    println(fr)

    val fid = FooId.createMutable(1)
    fr.setData(FooRecord._id, fid)
    println(fr)

    val frid = fr.getData(FooRecord._id)
    println(frid)

    fr.setData(FooRecord._id, BarId.createMutable(2))
    println(fr)
    println(fr.getData(FooRecord._id))
  }

}
