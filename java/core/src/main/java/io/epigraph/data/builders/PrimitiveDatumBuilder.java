/* Created by yegor on 7/26/16. */

package io.epigraph.data.builders;

import io.epigraph.data.PrimitiveDatum;

// TODO parameterize with native type (one of String, Integer, Long, Double, Boolean)?
// TODO remove this interface at all?
public interface PrimitiveDatumBuilder extends DatumBuilder, PrimitiveDatum {}
