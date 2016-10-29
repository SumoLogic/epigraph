/* Created by yegor on 10/8/16. */

package io.epigraph.wire;

import io.epigraph.data.Data;
import io.epigraph.data.Datum;
import io.epigraph.data.Val;
import io.epigraph.errors.ErrorValue;
import io.epigraph.projections.req.output.ReqOutputModelProjection;
import io.epigraph.projections.req.output.ReqOutputVarProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FormatReader<Exc extends Exception> {

  @NotNull Data readData(@NotNull ReqOutputVarProjection projection) throws Exc;

  @Nullable Datum readDatum(@NotNull ReqOutputModelProjection projection) throws Exc;

  @Nullable Data readData() throws Exc;

  @Nullable Datum readDatum() throws Exc;

  @NotNull Val readValue() throws Exc;

  @NotNull ErrorValue readError() throws Exc;

}
