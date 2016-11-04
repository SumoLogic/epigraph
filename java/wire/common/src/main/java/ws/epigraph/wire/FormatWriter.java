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

public interface FormatWriter<Exc extends Exception> {

  void writeData(@NotNull ReqOutputVarProjection projection, @Nullable Data data) throws Exc;

  void writeDatum(@NotNull ReqOutputModelProjection projection, @Nullable Datum datum) throws Exc;

  // FIXME take explicit type for all projectionless writes below (or add another set of methods that does):

  void writeData(@Nullable Data data) throws Exc;

  void writeValue(@NotNull Val value) throws Exc;

  void writeDatum(@Nullable Datum datum) throws Exc;

  void writeError(@NotNull ErrorValue error) throws Exc;

}
