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
import ws.epigraph.projections.req.*;
import ws.epigraph.wire.FormatWriter;
import ws.epigraph.wire.ReqFormatWriter;
import ws.epigraph.wire.WireFormat;
import ws.epigraph.wire.json.JsonFormat;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@NotThreadSafe
public class ReqJsonFormatWriter extends AbstractJsonFormatWriter<
    ReqProjection<?, ?>,
    ReqEntityProjection,
    ReqTagProjectionEntry,
    ReqModelProjection<?, ?, ?>,
    ReqRecordModelProjection,
    ReqFieldProjectionEntry,
    ReqFieldProjection,
    ReqMapModelProjection,
    ReqListModelProjection,
    ReqPrimitiveModelProjection,
    ReqKeyProjection
    > implements ReqFormatWriter {

  public ReqJsonFormatWriter(@NotNull OutputStream out) {
    this(out, StandardCharsets.UTF_8);
  }

  public ReqJsonFormatWriter(@NotNull OutputStream out, @NotNull Charset charset) {
    super(out, charset);
  }

  @Override
  protected @NotNull Datum keyDatum(final @NotNull ReqKeyProjection keyProjection) {
    return keyProjection.value();
  }

  /**
   * Builds a superset of all key projections. `null` is treated as wildcard and yields wildcard result immediately.
   */
  @Override
  protected @Nullable List<ReqKeyProjection> keyProjections(
      @NotNull Deque<ReqMapModelProjection> projections // non-empty
  ) {
    switch (projections.size()) {
      case 0:
        throw new IllegalArgumentException("No projections");
      case 1:
        return projections.peek().keys();
      default:
        List<ReqKeyProjection> keys = null;
        for (ReqMapModelProjection projection : projections) {
          List<ReqKeyProjection> projectionKeys = projection.keys();
          if (projectionKeys == null) return null;
          if (keys == null) keys = new ArrayList<>(projectionKeys);
          else keys.addAll(projectionKeys);
        }
        return keys;
    }
  }

  @Override
  protected @Nullable Collection<Datum> getExpectedKeys(final @NotNull ReqMapModelProjection mapProjection) {
    List<ReqKeyProjection> keys = mapProjection.keys();
    return keys == null ? null : keys.stream().map(ReqKeyProjection::value).collect(Collectors.toList());
  }

  @ThreadSafe
  public static final class ReqJsonFormatWriterFactory
      implements FormatWriter.Factory<ReqJsonFormatWriter> {

    @Contract(pure = true)
    @Override
    public @NotNull WireFormat format() {
      return JsonFormat.INSTANCE;
    }

    @Override
    public @NotNull ReqJsonFormatWriter newFormatWriter(@NotNull OutputStream out, @NotNull Charset charset) {
      return new ReqJsonFormatWriter(out, charset);
    }
  }

}
