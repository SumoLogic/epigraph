/* Created by yegor on 5/6/16. */

package ws.epigraph.med

trait MedSpecs {this: MedTypes with MedData =>


  trait VarSpec[V <: Var[V]] {

    def varType: VarType[V]

    def memberBranches: Seq[MemberBranch[V, _]]

  }


  trait MemberBranch[V <: Var[V], D <: Datum[D]] {

    def member: TypeMember[D]

    def dataSpec: DataSpec[D]

  }


  trait DataSpec[D <: Datum[D]] {

    def dataType: DataType[D]

    def params: Params

    // TODO: required? default? directives?

  }


  trait Params {

    def params: Seq[Param[_]]

  }


  trait Param[D <: Datum[D]] {

    def name: ParamDecl[D]

    def data: D // TODO some derivative of D?

  }


  trait ParamDecl[D <: Datum[D]] {

    def name: String // TODO ParamName?

    def spec: DataSpec[D]

  }


  trait RecordDatumSpec[D <: RecordDatum[D]] extends DataSpec[D] {

    override def dataType: RecordType[D]

    def fieldBranches: Seq[FieldBranch[D, _]]

  }


  trait FieldBranch[D <: RecordDatum[D], V <: Var[V]] {

    def field: Field[D, V]

    def varSpec: VarSpec[V]

  }

//trait UnionDatumSpec[D <: UnionDatum[D]] extends DataSpec[D] {
//  def tagBranches: Seq[TagBranch[D, _]]
//}
//trait TagBranch[D <: UnionDatum[D], VT <: VarType] {
//  def tag: Tag[D, VT]
//  def varSpec: VarSpec[VT]
//}

  trait PrimitiveDatumSpec[D <: PrimitiveDatum[D]] extends DataSpec[D] {

    override def dataType: PrimitiveType[D]

  }


  trait StringDatumSpec[D <: StringDatum[D]] extends PrimitiveDatumSpec[D] {

    override def dataType: StringType[D]

  }


}
