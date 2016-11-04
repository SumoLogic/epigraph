/* Created by yegor on 10/8/16. */

package ws.epigraph.wire;

import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.req.output.ReqOutputModelProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
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
