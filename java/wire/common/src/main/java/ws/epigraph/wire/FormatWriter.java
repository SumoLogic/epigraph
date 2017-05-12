/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 10/8/16. */

package ws.epigraph.wire;

import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenVarProjection;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

@NotThreadSafe
public interface FormatWriter<
    VP extends GenVarProjection<VP, ?, ?>,
    MP extends GenModelProjection</*MP*/?, ?, ?, ?>> extends AutoCloseable {

  void writeData(@NotNull VP projection, @Nullable Data data) throws IOException;

  void writeDatum(@NotNull MP projection, @Nullable Datum datum) throws IOException;

  // FIXME take explicit type for all projectionless writes below (or add another set of methods that does):

  void writeData(@Nullable Data data) throws IOException;

  void writeValue(@NotNull Val value) throws IOException;

  void writeDatum(@Nullable Datum datum) throws IOException;

  void writeError(@NotNull ErrorValue error) throws IOException;

  @Override
  default void close() throws IOException {}

  @ThreadSafe
  interface Factory<FW extends FormatWriter<?,?>> {
    @NotNull WireFormat format();

    @NotNull FW newFormatWriter(@NotNull OutputStream out, @NotNull Charset charset);
  }
}
