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

package ws.epigraph.wire.json.writer;

import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.projections.op.input.*;
import ws.epigraph.wire.OpInputFormatWriter;
import ws.epigraph.wire.WireFormat;
import ws.epigraph.wire.json.JsonFormat;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.List;

@NotThreadSafe
public class OpInputJsonFormatWriter extends AbstractJsonFormatWriter<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?, ?>,
    OpInputRecordModelProjection,
    OpInputFieldProjectionEntry,
    OpInputFieldProjection,
    OpInputMapModelProjection,
    OpInputListModelProjection,
    OpInputPrimitiveModelProjection,
    OpInputKeyProjection
    > implements OpInputFormatWriter {

  public OpInputJsonFormatWriter(@NotNull OutputStream out) {
    this(out, StandardCharsets.UTF_8);
  }

  public OpInputJsonFormatWriter(@NotNull OutputStream out, @NotNull Charset charset) {
    super(out, charset);
  }

  @Override
  protected @NotNull Datum keyDatum(final @NotNull OpInputKeyProjection keyProjection) {
    throw new UnsupportedOperationException();
  }

  /**
   * Builds a superset of all key projections. `null` is treated as wildcard and yields wildcard result immediately.
   */
  @Override
  protected @Nullable List<OpInputKeyProjection> keyProjections(
      @NotNull Deque<OpInputMapModelProjection> projections // non-empty
  ) { return null; }

  @ThreadSafe
  public static final class OpInputJsonFormatWriterFactory
      implements Factory<OpInputJsonFormatWriter> {

    @Contract(pure = true)
    @Override
    public @NotNull WireFormat format() {
      return JsonFormat.INSTANCE;
    }

    @Override
    public @NotNull OpInputJsonFormatWriter newFormatWriter(@NotNull OutputStream out, @NotNull Charset charset) {
      return new OpInputJsonFormatWriter(out, charset);
    }
  }

}
