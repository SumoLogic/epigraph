/* Created by yegor on 10/8/16. */

package io.epigraph.wire;

import io.epigraph.data.Data;
import io.epigraph.data.Datum;
import io.epigraph.projections.req.output.ReqOutputModelProjection;
import io.epigraph.projections.req.output.ReqOutputVarProjection;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FormatWriter<Exc extends Exception> {

  void write(@NotNull ReqOutputVarProjection projection, @Nullable Data data) throws Exc;

  <M extends DatumType> void write(@NotNull ReqOutputModelProjection<M> projection, @Nullable Datum datum) throws Exc;

}
