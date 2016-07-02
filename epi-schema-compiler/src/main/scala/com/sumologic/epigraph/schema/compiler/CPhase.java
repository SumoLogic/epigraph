package com.sumologic.epigraph.schema.compiler;/* Created by yegor on 6/27/16. */

public enum CPhase {

  PARSE,
  RESOLVE_TYPEREFS,
  COMPUTE_SUPERTYPES, // TODO rename to validate supertypes?
  INHERIT_FROM_SUPERTYPES;

//  public final int order;
//
//  CPhase(int order) {
//    this.order = order;
//  }

}
