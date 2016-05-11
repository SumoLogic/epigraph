/* Created by yegor on 5/6/16. */

package com.sumologic.epigraph.stencils

import com.sumologic.epigraph.gen

trait GenStencils {this: gen.GenNames with gen.GenTypes =>

  type GenVarStencil <: GenVarStencilApi

  type GenVarMemberBranch <: GenVarMemberBranchApi

  type GenDataStencil <: GenDataStencilApi

  type GenParams <: GenParamsApi

  type GenParam <: GenParamApi

  type GenParamStencil // either data (for requests), or stencil (for request templates)

  trait GenVarStencilApi {this: GenVarStencil =>

    def varType: GenVarType

    def branches: Seq[GenVarMemberBranch]

  }


  trait GenVarMemberBranchApi {this: GenVarMemberBranch =>

    def member: GenTypeMember

    def stencil: GenDataStencil

  }


  trait GenDataStencilApi {this: GenDataStencil =>

    def dataType: GenDataType

    def params: GenParams

  }


  trait GenParamsApi { this: GenParams =>

    def params: Seq[GenParam]

  }


  trait GenParamApi { this: GenParam =>

    def name: String // TODO ParamName?

    def stencil: GenParamStencil

  }


}

import com.sumologic.epigraph.raw

trait RawStencils extends GenStencils {this: raw.RawNames with raw.RawTypes =>

  type GenVarStencil = VarStencil

  type GenVarMemberBranch = VarMemberBranch


  class VarStencil(val varType: GenVarType, val branches: Seq[VarMemberBranch]) extends GenVarStencilApi


  class VarMemberBranch(override val member: GenTypeMember, override val stencil: GenDataStencil) extends GenVarMemberBranchApi


}